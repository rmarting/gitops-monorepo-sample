#!/bin/bash

export PROJECT="gitops-monorepo"
export CICD_PROJECT="$PROJECT-cicd"
export DEV_PROJECT="$PROJECT-dev"
export TST_PROJECT="$PROJECT-tst"
export PRO_PROJECT="$PROJECT-pro"

echo ""
echo "Deploying Pipelines and GitOps Operators"
echo ""

oc apply -f operators/ -n openshift-operators
sleep 5

echo ""
echo "Creating namespaces"
echo ""

oc new-project $DEV_PROJECT
oc new-project $TST_PROJECT
oc new-project $PRO_PROJECT
oc new-project $CICD_PROJECT
sleep 5

echo ""
echo "Allow pulling images from $CICD_PROJECT"
echo ""

oc policy add-role-to-user system:image-puller system:serviceaccount:$DEV_PROJECT:default -n $CICD_PROJECT
oc policy add-role-to-user system:image-puller system:serviceaccount:$TST_PROJECT:default -n $CICD_PROJECT
oc policy add-role-to-user system:image-puller system:serviceaccount:$PRO_PROJECT:default -n $CICD_PROJECT

echo ""
echo "Adding ArgoCD labels"
echo ""

oc label namespace $DEV_PROJECT argocd.argoproj.io/managed-by=argocd
oc label namespace $TST_PROJECT argocd.argoproj.io/managed-by=argocd
oc label namespace $PRO_PROJECT argocd.argoproj.io/managed-by=argocd

echo ""
echo "Deploying Sealed Secrets"
echo ""

helm repo add sealed-secrets https://bitnami-labs.github.io/sealed-secrets
helm upgrade --install sealed-secrets sealed-secrets/sealed-secrets --version 1.16.1 \
  -f sealed-secrets/values.yaml -n $CICD_PROJECT

echo "Installing kubeseal cli in tmp folder"

curl -sSL -o /tmp/kubeseal.tar.gz https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.17.2/kubeseal-0.17.2-linux-amd64.tar.gz
tar -xvf /tmp/kubeseal.tar.gz -C /tmp
/tmp/kubeseal --version

echo "Creating ClusterRole to edit Sealed Secrets"

oc apply -f sealed-secrets/sealedsecrets-edit-clusterrole.yaml
