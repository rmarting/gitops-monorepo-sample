# Pipelines Helm Chart

Helm Chart to deploy the pipelines used by this Product Monorepo.

This Helm Chart was tested with:

```shell
❯ helm version
version.BuildInfo{Version:"v3.7.2", GitCommit:"663a896f4a815053445eec4153677ddc24a0a361", GitTreeState:"clean", GoVersion:"go1.16.10"}
```

This repo was tested with the following latest versions of Red Hat OpenShift:

```shell
❯ oc version
Server Version: 4.10.5
Kubernetes Version: v1.23.3+e419edf
```

Main References:

* [Helm - The package manager for Kubernetes](https://helm.sh/)
* [Helm - Getting Started](https://helm.sh/docs/chart_template_guide/getting_started/)
* [Helm Version Support Policy](https://helm.sh/docs/topics/version_skew/)
* [Helm Commands](https://helm.sh/docs/helm/)

## Installing Pipelines

Install or Upgrade the pipelines with this Helm Chart for your current environment:

```shell
helm upgrade --install pipelines . \
    --history-max 4 \
    --namespace gitops-monorepo-cicd --create-namespace
```

## Removing application

To remove the helm chart

```shell
helm uninstall pipelines --namespace gitops-monorepo-cicd
```

## Pipelines Resources

### Config

The following resources are needed to execute the pipelines:

* Maven Settings to build, test and package the application
* PVCs to store and persist data across the pipeline runs: Workspaces, Maven Local Repo

### Sealed Secrets

The following sealed secret resources are needed to execute the pipelines:

* Git Auth Secret with the credentials to manage the Git repository of the application

To define different credentials, add in the `values.yaml` file, a list of different users
with an structure similar to:

```yaml
git:
  secrets:
    - name: github
      type: Opaque
      labels:
        credential.sync.jenkins.openshift.io: "true"
        sealedsecrets.bitnami.com/managed: "true"
      data:
        config: YOUR_SEALED_SECRETS_ENCRYPTED_DATA_VALUE
        credentials: YOUR_SEALED_SECRETS_ENCRYPTED_DATA_VALUE
```

The `YOUR_SEALED_SECRETS_ENCRYPTED_DATA_VALUE` value is generated using the `kubeseal` cli as:

1. Define a secret with the values of the credentials to secure. It could be similar to:

```shell
cat << EOF > /tmp/git-cli-auth-github.yaml
kind: Secret
apiVersion: v1
metadata:
  name: git-cli-auth-github
type: Opaque
stringData:
  .gitconfig: |
    [http]
      sslVerify = false
    [credential "https://github.com"]
      helper = store
  .git-credentials: |
    https://<YOUR_USERNAME>:<YOUR_TOKEN>@github.com
EOF
```

Then, get the sealed secret:

```shell
/tmp/kubeseal < /tmp/git-cli-auth-github.yaml > /tmp/sealed-git-cli-auth-github.yaml \
    -n gitops-monorepo-cicd \
    --controller-namespace gitops-monorepo-cicd \
    --controller-name sealed-secrets \
    -o yaml
```

Extract the sealed values of the `gitconfig` and `git-credentials` properties:

```shell
❯ cat /tmp/sealed-git-cli-auth-github.yaml | grep '\.git'
    .git-credentials: AgBULs....
    .gitconfig: AgAwSg....
```

Copy the `.git-credentials` and `.gitconfig` values into `values.yaml` file, and finally commit
and push the values to the repository, as this is GitOps.

### Tasks

The following tasks are defined to be used in the pipelines:

* [maven task](./templates/tasks/maven-task.yaml) to manage Maven commands and goals.
* [npm task](./templates/tasks/npm-task.yaml) to manage Node.JS and NPM commands and scripts.

### Pipelines

The following tasks are defined to be used in the pipelines:

* [Maven CICD pipeline](./templates/pipelines/maven-app-cicd-pipeline.yaml) to manage a full CICD for Maven applications.
* [Node.JS/NPM CICD pipeline](./templates/pipelines/node-app-cicd-pipeline.yaml) to manage a full CICD for Node.JS and NPM applications.

### Triggers

Deploy a set of different event listeners and triggers to start the different pipelines described above.

The main objects related with triggers are:

* `EventListener`: listens for events at a specified port on your OCP cluster.
  Specifies one or more `Triggers` or `TriggerTemplates`. Includes a route to be used in the
  WebHook.
* `Trigger`: specifies what happens when the `EventListener` detects an event.
  A `Trigger` specifies a `TriggerTemplate`, a `TriggerBinding`, and
  optionally, an Interceptor.
* `TriggerTemplate`: specifies a blueprint for the resource, such as a `TaskRun`
  or `PipelineRun`, that you want to instantiate and/or execute when your `EventListener`
  detects an event. It exposes parameters that you can use anywhere within your
  resource’s template.
* `TriggerBinding`: specifies the fields in the event payload from which you
  want to extract data and the fields in your corresponding `TriggerTemplate`
  to populate with the extracted values. You can then use the populated fields
  in the `TriggerTemplate` to populate fields in the associated `TaskRun` or `PipelineRun`.
* Create a `Secret` with the value to use from Git WebHook to create a secured call.

### Configuration and Values

The following table lists the configurable parameters of the Bootstrap chart and their default values. See the values file for more concrete examples.

| Parameter | Description | Default |
| --------- | ----------- | ------- |
| `project.name`  | Name of the product | `gitops-monorepo` |
| `application.frontend` | Name of the frontend application  | `sample-frontend` |
| `application.backend` | Name of the backend application  | `sample-backend` |
| `image.registry` | Route base of the Container Registry  | `image-registry.openshift-image-registry.svc:5000` |
| `image.group` | Group of the product in the Container Registry  | `gitops-monorepo-cicd` |
| `nexus.user` | User of Nexus | `admin` |
| `nexus.pass` | Password of user of Nexus | `admin123` |
| `nexus.url` | Location of Nexus | `http://sonatype-nexus-service:8081` |
| `sonarqube.user` | User of SonarQube | `admin` |
| `sonarqube.pass` | Password of user of SonarQube | `admin123` |
| `sonarqube.url` | Location of SonarQube | `http://sonarqube-sonarqube:9000` |
| `git.webhook.secret` | Secret value to use by the WebHooks | `s3cr3t` |
| `git.secrets` | Array to define the git credentials | Empty |

Each credential will have the following structure:

```yaml
    - name: github
      type: Opaque
      labels:
        credential.sync.jenkins.openshift.io: "true"
        sealedsecrets.bitnami.com/managed: "true"
      data:
        config: YOUR_SEALED_SECRETS_ENCRYPTED_DATA_VALUE
        credentials: YOUR_SEALED_SECRETS_ENCRYPTED_DATA_VALUE
```
