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
