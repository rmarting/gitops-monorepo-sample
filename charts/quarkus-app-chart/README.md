# Quarkus Helm Chart

Helm Charts to manage application deployments in Kubernetes or OpenShift.

This Helm Chart was tested with:

```shell
❯ helm version
version.BuildInfo{Version:"v3.8.0", GitCommit:"d14138609b01886f544b2025f5000351c9eb092e", GitTreeState:"clean", GoVersion:"go1.17.5"}
```

This repo was tested with the following latest versions of Red Hat OpenShift:

```shell
❯ oc version
Client Version: 4.9.0-202112142229.p0.g96e95ce.assembly.stream-96e95ce
Kubernetes Version: v1.22.3+e790d7f
```

Main References:

* [Helm - The package manager for Kubernetes](https://helm.sh/)
* [Helm - Getting Started](https://helm.sh/docs/chart_template_guide/getting_started/)
* [Helm Version Support Policy](https://helm.sh/docs/topics/version_skew/)
* [Helm Commands](https://helm.sh/docs/helm/)

## Deployment Topology

This Helm Chart will deploy a Quarkus-based application using the following OpenShift objects:

* [`DeploymentConfig`](./quarkus-app-chart/templates/deploymentconfig.yaml) to define the deployment of the application.
* [`Service`](./quarkus-app-chart/templates/service.yaml) to use the services defined by the application.
* [`Route`](./quarkus-app-chart/templates/route.yaml) to expose the application outside of OpenShift.
* [`ConfigMap`](./quarkus-app-chart/templates/configmap.yaml) and [`Secret`](./quarkus-app-chart/templates/secret.yaml) to
set up environment variables to inject into the pods deployed.
* [`HPA`](./quarkus-app-chart/templates/hpa.yaml) to allow autoscaling using OpenShift metrics.

The [`values.yaml`](./quarkus-app-chart/values.yaml) file includes samples of values to set up
the deployment of an application with this Helm Chart.

## Deploying Application

Install or Upgrade the application with this Helm Chart for your current environment, setting the
right value for `ENV` variable:

* `dev` defined to be used for a development environment in OCP
* `tst` defined to be used for a testing environment in OCP
* `pro` defined to be used for a production environment in OCP

Deploy the application:

```shell
helm upgrade --install sample-backend \
    ./quarkus-app-chart/ \
    --values ../argocd/applications/sample-backend/<ENV>/values.yaml \
    --history-max 4 \
    --namespace gitops-monorepo-<ENV> --create-namespace
```

## Removing application

To remove the helm chart:

```shell
helm uninstall sample-backend --namespace gitops-monorepo-<ENV>
```

### Configuration and Values

The following table lists the configurable parameters of the Bootstrap chart and their default values. See the values file for more concrete examples.

| Parameter | Description | Default |
| --------- | ----------- | ------- |
| `name`  | Name of the application | `quarkus-app` |
| `nameOverride` | Name of the application to override  | `quarkus-app` |
| `fullnameOverride` | Full name of the application  | empty |
| `replicaCount` | Number of replicas  | 1 |
| `image.repository` | Full route to the image in the Container Registry  | `image-registry.openshift-image-registry.svc:5000/gitops-monorepo-cicd` |
| `image.pullPolicy` | Pull policy  | `Always` |
| `image.tag` | Image Tag  | empty |
| `podAnnotations` | Pod annotations | empty |
| `podSecurityContext` | Security Context | empty |
| `env` | Array of environment variables | empty |
| `envFrom.configmap.enabled` | Load env variables from a ConfigMap | `true` |
| `envFrom.secrets.enabled` | Load env variables from a Secret | `true` |
| `securityContext` | Security context | empty |
| `nodeSelector` | Node selector | empty |
| `tolerations` | Array of tolerations | empty |
| `affinity` | Affinity | empty |
| `resources` | Resources and Limits | empty |
| `service.type` | Service type | `ClusterIP` |
| `service.port` | Service port | `8080` |
| `service.targetPort` | Service target port | `8080` |
| `route.enabled` | Declare a Route | `true` |
| `route.secure.enabled` | Declare a secure route | `true` |
| `route.secure.termination` | Termination of the secure route | `edge` |
| `autoscaling.enabled` | Enable autoscaling | `false` |
| `autoscaling.minReplicas` | Min replicas | `1` |
| `autoscaling.maxReplicas` | Max replicas | `100` |
| `autoscaling.targetCPUUtilizationPercentage` | Target CPU | `80` |
| `autoscaling.targetMemoryUtilizationPercentage` | Target Memory | `80` |

Environment variables will have the following structure:

```yaml
env:
  - name: SAMPLE_KEY
    value: "sample value"
  - name: SAMPLE_KEY_FROM_CM
    valueFrom:
      configMapKeyRef:
        name: quarkus-app-config
        key: SAMPLE_KEY_FROM_CM
  - name: SAMPLE_KEY_FROM_SECRET
    valueFrom:
      secretKeyRef:
        name: quarkus-app-config-secret
        key: SAMPLE_KEY_FROM_SECRET
```
