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
