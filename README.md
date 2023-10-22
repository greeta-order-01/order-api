### AWS Startup Template For Spring Boot Developers
#### Welcome to AWS Full-Stack Developer Template: React UI + Spring Boot + Terraform + Kubernetes + Keycloak Oauth2 Authorization Server + Github Actions + Spring Cloud Gateway + AWS SSL Certificate + External DNS + AWS Load Balancer Controller + Spring Cloud Kubernetes + Spring Boot Modulith + Swagger UI + Grafana Observability Stack

**Keycloak Administration Console** will be available here: **https://keycloak.yourdomain.com**

###### **admin user:** admin

###### **admin password:** admin

**Order UI**, secured with **Keycloak Server** will be available here: **https://order.yourdomain.com**

###### **manager user:** admin

###### **manager password:** admin

###### **regular user:** user

###### **regular password:** user

**Swagger UI Spring Cloud Gateway REST API Documentation**, secured with **Keycloak Server** will be available here: **https://orderapi.yourdomain.com**

###### **manager user:** admin

###### **manager password:** admin

###### **regular user:** user

###### **regular password:** user

###### **Oauth2 Client:** order-app


**Grafana Observability Stack**, will be available here: **https://grafana.yourdomain.com**

###### **user:** user

###### **password:** see step-08


### Step 01 - Clone repositories

**https://github.com/greeta-order-01/order-api** (API Source Code and Docker Images Repository)

**https://github.com/greeta-order-01/order-infra** (Terraform Infrastructure and GitOps Pipeline)

**https://github.com/greeta-order-01/order-ui** (UI Source Code and Docker Images Repository)

### Step-02: Prepare Your AWS Account

- make sure you have AWS Account with enough permissions

- make sure you have your own registered domain and hosted zone

-  create wildcard AWS Certificate for your domain: "*.yourdomain.com" (you will need ssl_certificate_arn later)

### Step-03: Prepare Your Github Account

- make sure you have your own Github Account or Organization

- clone order-ui, order-api and order-infra repositories to your github profile or organization

- In your cloned order-api Github Repository, go to Settings -> Secrets and Variables -> Actions -> New Repository Secret and create DISPATCH_TOKEN secret with the value of your personal github token (You need to create personal token in Developer Settings and make sure you give it workflow permissions)

- make sure your order-ui and order-api repository docker images are public by default (you need to change it in github settings: https://docs.github.com/en/packages/learn-github-packages/configuring-a-packages-access-control-and-visibility)

- in your order-ui repository, you will also need to change keycloak and api url in Constants.js. Please, read this article for more details: https://github.com/ivangfr/springboot-react-keycloak


### Step-04: Prepare API Source Code and Github Actions Workflow:

- go to the root directory of your cloned order-ui, order-api, order-infra github repository

- Edit "**.github/workflows**" files: replace "**greeta-order-01**" with the name of your github profile or organization; replace "**order-ui, order-api and order-infra**" with the names of your cloned or forked repositories (or leave the names like this if you don't want to change the names); replace "**master**" with the name of your main branch (or leave it like this, if you don't want to change, but please, note that you would have to change default main branch name in github settings)


### Step-05: Prepare Terraform Infrastructure:

- go to the root directory of your cloned order-infra github repository

- create terraform.auto.tfvars in your order-infra repository and provide your own aws_region and ssl_certificate_arn

```
aws_region = "eu-central-1"
environment = "dev"
business_division = "it"
cluster_name = "order-cluster"
ssl_certificate_arn = "arn:aws:acm:eu-central-1:your-certificate-arn"
```

- replace "greeta.net" in terraform files of order-infra repository, with the name of your domain (please, use search to find all files, where "greeta.net" is used)

- Commit your order-infra changes to github (don't worry, terraform.auto.tfvars is in .gitignore and it won't be committed)
```
git add .
git commit -m "your comment"
git push origin
````

### Step-06: Build Docker Images with Github Actions

- go to the root directory of your cloned order-ui, order-api github repository

- Commit your order-ui or order-api changes to github (it should trigger creation of docker images pipeline and for order-api changes it should also trigger order-infra pipeline)
```
git add .
git commit -m "your comment"
git push origin
````

- wait until order-api pipeline in github is finished and order-infra pipeline is started

- order-infra pipeline automatically changes docker image versions to the versions of docker images, created in order-api pipeline and pushes new docker image versions to order-infra repository


### Step-07: Deploy Infrastructure to AWS with Terraform:

- go to the root directory of your cloned order-infra github repository

- pull changes from orders-infra repository and run terraform

```
git pull
terraform apply --auto-approve
```

- if terraform script is failed during creation of grafana observability stack, please, run terraform apply --auto-approve again (it sometimes happens when kubernetes cluster is not ready yet)

- grafana observability stack will be available by url: https://grafana.yourdomain.com; username: user; password: you should see the password in the output of terraform script. Sometimes it is empty. In this case, you can get the password with this command:

```
kubectl get secret --namespace observability-stack loki-stack-grafana -o jsonpath="{.data.admin-password}" | base64 --decode;
```

### Step-08: Test your Microservices:

- go to "**https://orderapi.yourdomain.com**"

- you should see successfully loaded "**Swagger UI REST API Documentation**" page with drop-down selection of microservices
- Select Order or ERP microservice from the drop-down list
- Click **Authorize** button and login with admin/admin (full access) or user/user (limited access)
- In **Authorize** dialog window you should also provide the name of the OAuth2 Client (**order-app** )
- After successfull authorization, try any REST API endpoint
- Go to https://grafana.yourdomain.com and find the logs and traces, generated by the endpoints (Find "Explore" menu, then go to "Loki", select "app" and then select the name of the microservice and then "Run Query")
- Test Order UI with keycloak authorization and redirect login page:
- Go to "**https://order.yourdomain.com**" and login with admin/admin (full access) or user/user (limited access) (see https://github.com/ivangfr/springboot-react-jwt-token for more details, but please note that we are using keycloak authorization, instead of internal jwt token authorization, described in this article)


##### Congratulations! You sucessfully tested Cloud-Native Microservices GitOps Pipeline on AWS with Terraform, Kubernetes, Spring Cloud Gateway and Keycloak!
- ##### Now you can deploy your own docker containers to AWS EKS cluster!
- ###### You also implemented AWS Load Balancer Controller with External DNS and SSL, which act as a Gateway for your microservices and automatically creates sub-domains, using your wildcard AWS Certificate)
- ###### You also implemented Spring Cloud Gateway and Swagger UI REST API Documentation for your microservices, which allows you to select REST API Documentation Microservices from the drop-down list and authorize with Keycloak
- ###### Now you can add any number of microservices to your Kubernetes Cluster and use only one Kubernetes Ingress Load Balancer and Spring Cloud Gateway for all these microservices

- ##### You successfully deployed Keycloak Authorization Server, which protects your Swagger UI REST API Documentation Page and your UI
- ##### Spring Security seamlessly handled the entire process of calling the Keycloak OAuth2 Authorization Server to authenticate the user
- ###### Now you can protect any number of microservices by your Keycloak Server and use Single Sign-On Authentication for all these microservices


### Step-09: Clean-Up:

Please make sure you run terraform-destroy.sh script, instead of just calling terraform destroy (otherwise you will have issues with deletion of kubernetes ingress resources by terraform)

```
sh terraform-destroy.sh  
```
