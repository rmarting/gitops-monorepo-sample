# Bootstrapping Red Hat OpenShift Container Platform

This section includes the main steps to bootstrap you OCP environment.

**TL;DR**: The [bootstrap.sh](./bootstrap.sh) file includes the commands to set up your environment.

## Pipelines and GitOps Operators

The following OpenShift Operators are required to execute many of the automated tasks
of this repository:

* OpenShift Pipelines (Tekton)
* OpenShift GitOps (ArgoCD)

To deploy the operators:

```shell
oc apply -f ./operators -n openshift-operators
```

To check the deployment status:

```shell
❯ oc get csv
NAME                                     DISPLAY                       VERSION   REPLACES                            PHASE
openshift-gitops-operator.v1.4.3         Red Hat OpenShift GitOps      1.4.3     openshift-gitops-operator.v1.4.2    Succeeded
openshift-pipelines-operator-rh.v1.6.2   Red Hat OpenShift Pipelines   1.6.2     redhat-openshift-pipelines.v1.5.2   Succeeded
```

## Namespace topology

The application will be promoted from the source code repository to a set of different
namespaces, with the following topology:

* **gitops-monorepo-cicd**: Namespace to manage the CICD tools
* **gitops-monorepo-dev** represents a development environment
* **gitops-monorepo-tst** represents a testing/staging environment
* **gitops-monorepo-pro** represents the production environment

**NOTE:** This step must be done with a `cluster-admin` user.

For CICD environment:

```shell
❯ oc new-project gitops-monorepo-cicd
❯ oc label namespace gitops-monorepo-cicd argocd.argoproj.io/managed-by=argocd
```

For development environment:

```shell
❯ oc new-project gitops-monorepo-dev
❯ oc label namespace gitops-monorepo-dev argocd.argoproj.io/managed-by=argocd
❯ oc policy add-role-to-user system:image-puller system:serviceaccount:gitops-monorepo-dev:default -n gitops-monorepo-cicd
```

For testing environment:

```shell
❯ oc new-project gitops-monorepo-tst
❯ oc label namespace gitops-monorepo-tst argocd.argoproj.io/managed-by=argocd
❯ oc policy add-role-to-user system:image-puller system:serviceaccount:gitops-monorepo-tst:default -n gitops-monorepo-cicd
```

For production environment:

```shell
❯ oc new-project gitops-monorepo-pro
❯ oc label namespace gitops-monorepo-pro argocd.argoproj.io/managed-by=argocd
❯ oc policy add-role-to-user system:image-puller system:serviceaccount:gitops-monorepo-pro:default -n gitops-monorepo-cicd
```

As the images of the different applications will be built in the CICD namespace, it is needed to allow to pull
images from there for the rest of namespaces. This command for each namespace will allow it:

```shell
❯ oc policy add-role-to-user system:image-puller system:serviceaccount:gitops-monorepo-<ENV>:default -n gitops-monorepo-cicd
```

## DevOps Teams

As it is now a good idea to allow DevOps teams to be `cluster-admin`, we could add these users to the new
namespaces created the following commands:

```shell
oc adm policy add-role-to-user edit <USER> -n gitops-monorepo-<ENV>
```

## Nexus and Limit Ranges in RHPDS

If you are using an OCP Cluster provided by the [Red Hat Product Demo System](https://rhpds.redhat.com/), then you could
find issues with Nexus related with the limit ranges applied by default. To avoid any issue about that, it is
recommended to adapt the limit ranges in the CICD namespace to allow to use more resources.

This command will increase the limit range in the CICD namespace:

```shell
oc patch limitrange gitops-monorepo-cicd-core-resource-limits \
     -p '{"spec":{"limits":[{"default":{"cpu":"500m","memory":"1536Mi"},"defaultRequest":{"cpu":"500m","memory":"1536Mi"},"max":{"cpu":"4","memory":"6Gi"},"type": "Container"},{"max":{"cpu":"6","memory":"12Gi"},"type":"Pod"}]}}' \
     -n gitops-monorepo-cicd
```
