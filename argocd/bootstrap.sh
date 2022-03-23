#!/bin/bash

export PROJECT="gitops-monorepo"
export CICD_PROJECT="$PROJECT-cicd"
export DEV_PROJECT="$PROJECT-dev"
export TST_PROJECT="$PROJECT-tst"
export PRO_PROJECT="$PROJECT-pro"

echo ""
echo "Moving to $CICD_PROJECT namespace"
echo ""

oc project $CICD_PROJECT

echo ""
echo "Adding namespaces managed by OpenShift GitOps operator"
echo ""

export ARGOCD_NS="$(oc get csv/openshift-gitops-operator.v1.4.3 -n openshift-operators \
    -o jsonpath='{.spec.install.spec.deployments[?(@.name=="gitops-operator-controller-manager")].spec.template.spec.containers[?(@.name=="manager")].env[?(@.name=="ARGOCD_CLUSTER_CONFIG_NAMESPACES")].value}'),gitops-monorepo-cicd"

oc patch csv openshift-gitops-operator.v1.4.3 -n openshift-operators \
    --type='json' \
    -p '[{"op": "replace", "path": "/spec/install/spec/deployments/0/spec/template/spec/containers/0/env/22", "value": {"name": "ARGOCD_CLUSTER_CONFIG_NAMESPACES", "value":"'${ARGOCD_NS}'"}}]'

echo ""
echo "Disabling default ArgoCD instance"
echo ""

oc patch csv openshift-gitops-operator.v1.4.3 -n openshift-operators \
    --type=json \
    -p '[{"op": "add", "path": "/spec/install/spec/deployments/0/spec/template/spec/containers/0/env/-", "value": {"name":"DISABLE_DEFAULT_ARGOCD_INSTANCE","value":"true"}}]'

echo ""
echo "Deploying ArgoCD instance"
echo ""

oc apply -f argocd.yaml -n $CICD_PROJECT
sleep 10

echo ""
echo "Waiting for application pods are ready"
echo ""

oc wait pod -lapp.kubernetes.io/name="argocd-server" \
    --for=condition=Ready --timeout=180s \
    -n $CICD_PROJECT
sleep 5

echo ""
echo "Login in ArgoCD as admin"
echo ""

argocd login $(oc get route argocd-server -o jsonpath='{.spec.host}' -n gitops-monorepo-cicd) \
  --username admin \
  --password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d) \
  --insecure

echo ""
echo "Creating new users in ArgoCD"
echo ""

oc apply -f users/argocd-cm.yaml -n $CICD_PROJECT
sleep 5

echo ""
echo "Updating passwords for new users in ArgoCD"
echo ""

argocd account update-password \
    --account operator \
    --new-password passw0rd123 \
    --current-password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d)
argocd account update-password \
    --account developer \
    --new-password passw0rd123 \
    --current-password $(oc get secret argocd-cluster -o jsonpath='{.data.admin\.password}' -n gitops-monorepo-cicd | base64 -d)

echo ""
echo "Updating RBAC in ArgoCD"
echo ""

oc apply -f users/argocd-rbac-cm.yaml -n $CICD_PROJECT
sleep 5

echo ""
echo "Login in ArgoCD as operator"
echo ""

argocd login $(oc get route argocd-server -o jsonpath='{.spec.host}' -n gitops-monorepo-cicd) \
  --username operator \
  --password passw0rd123 \
  --insecure

echo ""
echo "Allowing ArgoCD to operate the Application's Namespaces"
echo ""

oc adm policy add-role-to-user admin system:serviceaccount:$CICD_PROJECT:argocd-argocd-application-controller -n $DEV_PROJECT
oc adm policy add-role-to-user admin system:serviceaccount:$CICD_PROJECT:argocd-argocd-application-controller -n $TST_PROJECT
oc adm policy add-role-to-user admin system:serviceaccount:$CICD_PROJECT:argocd-argocd-application-controller -n $PRO_PROJECT

echo ""
echo "Enabling ArgoCD to manage Sealed Secrets"
echo ""

oc adm policy add-role-to-user sealedsecrets-edit system:serviceaccount:$CICD_PROJECT:argocd-argocd-application-controller

echo ""
echo "Deploying Nexus by ArgoCD"
echo ""

oc apply -f tools/sonatype-nexus/application.yaml -n $CICD_PROJECT

echo ""
echo "Deploying SonarQube by ArgoCD"
echo ""

oc apply -f tools/sonarqube/application.yaml -n $CICD_PROJECT

echo ""
echo "Deploying Pipelines by ArgoCD"
echo ""

oc apply -f tools/pipelines/application.yaml -n $CICD_PROJECT

echo ""
echo "Creating Project definitions in ArgoCD"
echo ""

oc apply -f projects/ -n $CICD_PROJECT
oc apply -f application/sample-app-set.yaml -n $CICD_PROJECT
