### Cloud-Native Microservices GitOps Pipeline on AWS with Spring Boot, Terraform, Kubernetes, Keycloak Oauth2 Authorization Server, Github Actions, Spring Cloud Gateway, AWS SSL Certificate, External DNS, Nginx Ingress Controller, Spring Cloud Kubernetes, Swagger UI REST API Documentation and Grafana Observability Stack

**Keycloak Administration Console** will be available here: **https://keycloak.yourdomain.com**

###### **admin user:** admin

###### **admin password:** admin

**Movies Online UI**, secured with **Keycloak Server** will be available here: **https://movie.yourdomain.com**

###### **manager user:** admin

###### **manager password:** admin

###### **regular user:** user

###### **regular password:** user

**Swagger UI Spring Cloud Gateway REST API Documentation**, secured with **Keycloak Server** will be available here: **https://movie.yourdomain.com**

###### **manager user:** admin

###### **manager password:** admin

###### **regular user:** user

###### **regular password:** user

###### **Oauth2 Client:** movie-app


**Grafana Observability Stack**, will be available here: **https://grafana.yourdomain.com**

###### **user:** user

###### **password:** see step-08


### Step 01 - Clone repositories

**https://github.com/greeta-movie/movies-api** (API Source Code and Github Docker Images Pipeline)

**https://github.com/greeta-movie/movies-infra** (Terraform Infrastructure and GitOps Pipeline)

**https://github.com/greeta-movie/movies-ui** (UI Source Code and Github Docker Image Pipeline)

### Step-02: Prepare Your AWS Account

- make sure you have AWS Account with enough permissions

- make sure you have your own registered domain and hosted zone

-  create wildcard AWS Certificate for your domain: "*.yourdomain.com" (you will need ssl_certificate_arn later)

### Step-03: Prepare Your Github Account

- make sure you have your own Github Account or Organization

- clone movies-api and movies-infra repositories to your github profile or organization

- In your cloned movies-api Github Repository, go to Settings -> Secrets and Variables -> Actions -> New Repository Secret and create DISPATCH_TOKEN secret with the value of your personal github token (You need to create personal token in Developer Settings and make sure you give it workflow permissions)

- if you use github organization, then you need to make github docker image packages public by default (not sure how to do it if you use github profile directly, but if github docker images are not public by default, you also need to change it in github settings)

- in github organization settings, go to packages -> Package Creation -> set public as defult (skip it, if you use github profile directly, but I'm not 100% sure, please, refer to github actions docker images documentation, if you have any issues)

- if you want to create your own UI docker image, you should also clone movies-ui repository (instructions for creation of docker image pipeline are similar to movies-api, but you will also need to change keycloak and api url in constants.js and provide your omdb account secret in env.local file. Please, read this article for more details: https://github.com/ivangfr/springboot-react-keycloak


### Step-04: Prepare API Source Code and Github Actions Workflow:

- go to the root directory of your cloned movies-api github repository

- Edit "**.github/workflows**" files: replace "**greeta-movie**" with the name of your github profile or organization; replace "**movies-api and movies-infra**" with the names of your cloned or forked repositories (or leave the names like this if you don't want to change the names); replace "**master**" with the name of your main branch (or leave it like this, if you don't want to change, but please, note that you would have to change default main branch name in github settings)


### Step-05: Prepare Terraform Infrastructure:

- go to the root directory of your cloned movies-infra github repository

- create terraform.auto.tfvars in your movies-infra repository and provide your own aws_region and ssl_certificate_arn

```
aws_region = "eu-central-1"
environment = "dev"
business_division = "it"
cluster_name = "movie-cluster"
ssl_certificate_arn = "arn:aws:acm:eu-central-1:your-certificate-arn"
```

- replace "greeta.net" in terraform files of movies-infra repository, with the name of your domain (please, use search to find all files, where "greeta.net" is used)

- Commit your movies-infra changes to github (don't worry, terraform.auto.tfvars is in .gitignore and it won't be committed)
```
git add .
git commit -m "your comment"
git push origin
````

### Step-06: Build Docker Images with Github Actions

- go to the root directory of your cloned movies-api github repository

- Commit your movies-api changes to github (it should trigger creation of docker images pipeline and then movies-infra pipeline)
```
git add .
git commit -m "your comment"
git push origin
````

- wait until movies-api pipeline in github is finished and movies-infra pipeline is started

- movies-infra pipeline automatically changes docker image versions to the versions of docker images, created in movies-api pipeline and pushes new docker image versions to movies-infra repository


### Step-07: Deploy Infrastructure to AWS with Terraform:

- go to the root directory of your cloned movies-infra github repository

- pull changes from movies-infra repository and run terraform

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

- go to "**https://movie.yourdomain.com**"

- you should see successfully loaded "**Swagger UI REST API Documentation**" page with drop-down selection of microservices
- Select any microservice from the drop-donw list
- Click **Authorize** button and login with admin/admin (full access) or user/user (read-only access)
- In **Authorize** dialog window you should also provide the name of the OAuth2 Client (**movie-app** )
- After successfull authorization, try any REST API endpoint
- Go to https://grafana.yourdomain.com and find the logs and traces, generated by the endpoints (Find "Explore" menu, then go to "Loki", select "app" and then select the name of the microservice and then "Run Query")

- as a bonus, you can clone "movies-ui" repository and test "movie" microservice UI with keycloak authorization and redirect login page (see https://github.com/ivangfr/springboot-react-keycloak for more details)

- go to "**https://movie.yourdomain.com**" and login with admin/admin (full access) or user/user (read-only access) (see https://github.com/ivangfr/springboot-react-keycloak for more details)



##### Congratulations! You sucessfully tested Cloud-Native Microservices GitOps Pipeline on AWS with Terraform, Kubernetes, Spring Cloud Gateway and Keycloak!
- ##### Now you can deploy your own docker containers to this cluster!
- ##### You significantly reduced your AWS bills by skipping NAT gateway! (but, please, note that for real production usage you should create privatte subnets and NAT Gateway)
- ###### You also implemented Nginx Ingress Controller with External DNS and SSL, which act as a Gateway for your microservices and automatically create sub-domains, using your wildcard AWS Certificate)
- ###### You also implemented Spring Cloud Gateway and Gateway REST API Documentation for your microservices, which allows you to select REST API Documentation Microservice from the drop-down list and authorize with Keycloak
- ###### Now you can add any number of microservices to your Kubernetes Cluster and use only one Nginx Ingress Controller and Spring Cloud Gateway for all these microservices

- ##### You successfully deployed Keycloak Authorization Server, which protects your Swagger UI REST API Documentation Page and your UI
- ##### Spring Security seamlessly handled the entire process of calling the Keycloak OAuth2 Authorization Server to authenticate the user
- ###### Now you can protect any number of microservices by your Keycloak Server and use Single Sign-On Authentication for all these microservices


### Step-09: Clean-Up:

```
terraform destroy --auto-approve  
```
