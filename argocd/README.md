# ArgoCD

Now it is time to deploy our ArgoCD instance to manage our product, and any component
or process needed.

**TL;DR**: The [bootstrap.sh](bootstrap.sh) file includes the commands to set up your environment.

**NOTE:** This step must done with a `cluster-admin` user as we need to setup some properties
in the Red Hat OpenShift GitOps operator already deployed.

* Setup the GitOps operator to manage the `gitops-monorepo-cicd` namespace and disabled the
cluster wide instance

```shell
export ARGOCD_NS="$(oc get csv/openshift-gitops-operator.v1.4.3 -n openshift-operators \
    -o jsonpath='{.spec.install.spec.deployments[?(@.name=="gitops-operator-controller-manager")].spec.template.spec.containers[?(@.name=="manager")].env[?(@.name=="ARGOCD_CLUSTER_CONFIG_NAMESPACES")].value}'),gitops-monorepo-cicd"
```

* Patching operator to manage the other namespaces, like `gitops-monorepo-cicd`:

```shell
oc patch csv openshift-gitops-operator.v1.4.3 -n openshift-operators \
    --type='json' \
    -p '[{"op": "replace", "path": "/spec/install/spec/deployments/0/spec/template/spec/containers/0/env/22", "value": {"name": "ARGOCD_CLUSTER_CONFIG_NAMESPACES", "value":"'${ARGOCD_NS}'"}}]'
```

If you want to uninstall the default ArgoCD instance, add the following env variable in the CSV:

```shell
oc patch csv openshift-gitops-operator.v1.4.3 -n openshift-operators --type=json \
    -p '[{"op": "add", "path": "/spec/install/spec/deployments/0/spec/template/spec/containers/0/env/-", "value": {"name":"DISABLE_DEFAULT_ARGOCD_INSTANCE","value":"true"}}]'
```

The definition of the operator, from `openshift-operators` namespace, should be similar to:

```yaml
apiVersion: operators.coreos.com/v1alpha1
kind: ClusterServiceVersion
metadata:
spec:
  install:
    spec:
      deployments:
        - name: gitops-operator-controller-manager
          spec:
            template:
              spec:
                containers:
                  - name: manager
                    env:
                      - name: ARGOCD_CLUSTER_CONFIG_NAMESPACES
                        value: 'openshift-gitops,gitops-monorepo-cicd'
                      - name: DISABLE_DEFAULT_ARGOCD_INSTANCE
                        value: 'true'
```

* *Deploy a new instance of ArgoCD in the `gitops-monorepo-cicd` namespace to deploy applications:

```shell
oc apply -f argocd.yaml -n gitops-monorepo-cicd
```

To access to the new ArgoCD instance:

```shell
echo https://$(oc get route argocd-server -o jsonpath='{.spec.host}' -n gitops-monorepo-cicd)
```

Also you could login with the default `admin` user created (the default password is stored in a secret):

```shell
argocd login $(oc get route argocd-server -o jsonpath='{.spec.host}' -n gitops-monorepo-cicd) \
  --username admin \
  --password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d) \
  --insecure
```

To create local users handled internally by ArgoCD, they are defined from `ConfigMap` and use the
`argocd` cli to setup the password. To create some users:

```shell
oc apply -f users/argocd-cm.yaml -n gitops-monorepo-cicd
```

To create the password for the new users:

```shell
❯ argocd account update-password \
    --account operator \
    --new-password passw0rd123 \
    --current-password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d)
❯ argocd account update-password \
    --account developer \
    --new-password passw0rd123 \
    --current-password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d)
```

Now we update RBAC policies to match the new roles to the users:

```shell
oc apply -f users/argocd-rbac-cm.yaml -n gitops-monorepo-cicd
```

Now we can login with the `operator` user created:

```shell
argocd login $(oc get route argocd-server -o jsonpath='{.spec.host}' -n gitops-monorepo-cicd) \
  --username operator \
  --password passw0rd123 \
  --insecure
```

To more reference about local users, please, refer to the [documentation](https://argo-cd.readthedocs.io/en/stable/operator-manual/user-management/#local-usersaccounts-v15).

## Common CICD Tools

Our Product Monorepo requires a set of common tools to cover our Software Delivery Cycle. These
tools are:

* Nexus to manage the different artifacts of our product
* SonarQube to measure the quality of our product

All these tools will be deployed and managed by ArgoCD, as just this is GitOps.

### Nexus

To deploy Nexus by ArgoCD, create the application definition as:

```shell
oc apply -f tools/sonatype-nexus/application.yaml -n gitops-monorepo-cicd
```

### SonarQube

To deploy SonarQube by ArgoCD, create the application definition as:

```shell
oc apply -f tools/sonarqube/application.yaml -n gitops-monorepo-cicd
```

## Application Namespaces

**NOTE:** This step must be done with a `cluster-admin` user.

For development environment:

```shell
oc adm policy add-role-to-user admin system:serviceaccount:gitops-monorepo-cicd:argocd-argocd-application-controller -n gitops-monorepo-dev
```

For testing environment:

```shell
oc adm policy add-role-to-user admin system:serviceaccount:gitops-monorepo-cicd:argocd-argocd-application-controller -n gitops-monorepo-tst
```

For production environment:

```shell
oc adm policy add-role-to-user admin system:serviceaccount:gitops-monorepo-cicd:argocd-argocd-application-controller -n gitops-monorepo-pro
```

## Product Monorepo Projects

ArgoCD uses the concept of project to classify the different applications managed by it. Our
Product Monorepo will have the following project definitions:

* `gitops-monorepo-tools` to manage the different tools related with our product
* `gitops-monorepo` to manage the different applications of our product

To create the projects definitions:

```shell
oc apply -f projects/ -n gitops-monorepo-cicd
```

The applications will be created by the CICD pipelines, but you could create them
with the following commands:

* Create the new application to deploy:

Applying the `application` CR object describing the application:

```shell
oc apply -f application/<ENV>/application.yaml -n gitops-monorepo-cicd
```

Or use the `argocd` CLI to create the definition of the application:

```shell
argocd app create sample-app-<ENV> \
  --dest-namespace gitops-monorepo-<ENV> \
  --dest-server https://kubernetes.default.svc \
  --repo https://github.com/rmarting/gitops-monorepo-sample.git \
  --path "charts/quarkus-app-chart" \
  --sync-policy automated --self-heal --auto-prune
```

Choose the right value for `ENV` variable:

* `dev` defined the development environment.
* `tst` defined the testing environment.
* `pro` defined the production environment.

Verify the applications are synchronized in the final environments:

```shell
❯ oc get applications -n gitops-monorepo-cicd
NAME             SYNC STATUS   HEALTH STATUS
nexus            Synced        Healthy
pipelines        Synced        Healthy
sample-app-dev   Synced        Healthy
sample-app-pro   Synced        Healthy
sample-app-tst   Synced        Healthy
sonarqube        Synced        Healthy
```

## Webhook

ArgoCD polls Git repositories every some minutes to detect changes to the manifests.
To eliminate this delay from polling, the API server can be configured to receive webhook
events. The following explains how to configure a Git webhook.

First we need to edit the `argocd-secret` secret of ArgoCD in the `gitops-monorepo-cicd` namespace
to add the secret value to use from the Webhook.

The secret could be updated by the Web Console or by the command line:

```shell
oc edit secret argocd-secret -n gitops-monorepo-cicd
```

And a new entry to setup the secret:

```yaml
  # git webhook secret
  webhook.github.secret: shhhh! it's a git secret
```

That value will be used in the Webhook definition in the Git server. To create a new
webhook navigate to WebHook settings in your repository:

Fulfill the form with these values:

* **Payload URL**: Your external IP Address from the route with `/api/webhook` path.
* **HTTP Method**: POST
* **Content type**: application/json
* **Secret**: Value defined by `webhook.github.secret` value

From now every changed pushed into the repository will trigger a sync operation in ArgoCD.

References: 

* [Git Webhook Configuration](https://argo-cd.readthedocs.io/en/stable/operator-manual/webhook/)
