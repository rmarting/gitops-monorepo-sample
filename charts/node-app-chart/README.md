# Helm Charts

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

This Helm Chart will deploy a Node/Angular-based application using the following OpenShift objects:

* [`DeploymentConfig`](./node-app-chart/templates/deploymentconfig.yaml) to define the deployment of the application.
* [`Service`](./node-app-chart/templates/service.yaml) to use the services defined by the application.
* [`Route`](./node-app-chart/templates/route.yaml) to expose the application outside of OpenShift.
* [`ConfigMap`](./node-app-chart/templates/configmap.yaml) and [`Secret`](./node-app-chart/templates/secret.yaml) to
set up environment variables to inject into the pods deployed.
* [`HPA`](./node-app-chart/templates/hpa.yaml) to allow autoscaling using OpenShift metrics.

The [`values.yaml`](./node-app-chart/values.yaml) file includes samples of values to set up
the deployment of an application with this Helm Chart.

## Deploying Application

Install or Upgrade the application with this Helm Chart for your current environment, setting the
right value for `ENV` variable:

* `dev` defined to be used for a development environment in OCP
* `int` defined to be used for an integration environment in OCP

Deploy the application in Development (`dev`) environment:

```shell
helm upgrade --install sample-frontend \
    ./node-app-chart/ \
    --values ../../argocd/applications/sample-frontend/<ENV>/values.yaml \
    --history-max 4 \
    --namespace gitops-monorepo-<ENV> --create-namespace
```

## Removing application

To remove the helm chart

```shell
helm uninstall sample-frontend --namespace gitops-monorepo-<ENV>
```
