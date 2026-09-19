# DevOps Demo

A deliberately small Spring Boot application used as the running example in a five-day DevOps course. It shows how a build-time version and runtime environment configuration behave differently while providing a database, health probes, metrics, tests, an SBOM, and a safe place to practise deployment work.

## Build and run

```sh
./mvnw clean package
java -jar target/*.jar
```

Select an environment profile or override its color directly:

```sh
SPRING_PROFILES_ACTIVE=staging java -jar target/*.jar
APP_COLOR='#8b5cf6' java -jar target/*.jar
```

`application.yml` supplies defaults, `APP_COLOR` overrides them, and `--app.color=...` has the highest standard Spring precedence. Use `./mvnw clean package -Pfast` to skip tests for a build-speed demonstration.

## Change a color and push the code

For a committed configuration change, edit `app.color` in `src/main/resources/application.yml` for the default color, or in a profile file such as `src/main/resources/application-staging.yml`. Hex colors must be quoted in YAML:

```yaml
app:
  color: "#8b5cf6"
```

Build and check the change locally, then commit and push it to GitHub:

```sh
./mvnw clean verify
git add src/main/resources/application.yml
git commit -m "Change default page color"
git push
```

Changing YAML is a configuration commit; it does not change application Java code. To demonstrate a runtime-only change without a commit or rebuild, use `APP_COLOR='#8b5cf6' java -jar target/*.jar` instead.

To release a new version, change the `<version>` in `pom.xml` and run `./mvnw clean package`; the new version, build time, and Git short SHA are embedded in the jar. The build timestamp intentionally changes on every build (`project.build.outputTimestamp` is not configured). The SHA is blank only when the source is built outside a Git checkout. The same jar produces every environment on purpose: only runtime configuration changes the environment and color.

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `SPRING_PROFILES_ACTIVE` | none (`local`) | Selects `dev`, `staging`, `prod`, or `postgres` profile |
| `APP_COLOR` | `slate` | Page background color |
| `DB_HOST` | `localhost` | PostgreSQL host (`postgres` profile) |
| `DB_PORT` | `5432` | PostgreSQL port (`postgres` profile) |
| `DB_NAME` | `devops_demo` | PostgreSQL database (`postgres` profile) |
| `DB_USER` | `postgres` | PostgreSQL user (`postgres` profile) |
| `DB_PASSWORD` | `postgres` | PostgreSQL password (`postgres` profile) |
| `HOSTNAME` | machine hostname | Hostname displayed by the page |

The default is an in-memory H2 database with a Flyway migration. Enable the `postgres` profile when a PostgreSQL service is available. On the `postgres` profile, the application exits at startup if PostgreSQL is unreachable because Flyway runs during boot; no retry workaround is used. After startup, readiness becomes `DOWN` only if the database connection is lost, while liveness remains independent of the database.

## Docker (Module 5)

Build the application image and run the staging profile:

```sh
docker build -t devops-demo:1.0.0 .
docker run -d --name devops-demo -p 8080:8080 -e SPRING_PROFILES_ACTIVE=staging devops-demo:1.0.0
```

Start the full application and PostgreSQL stack:

```sh
cp .env.example .env && docker compose up -d --build
curl -s localhost:8080/api/info
docker compose ps
```

Stop the stack with `docker compose down`. Add `-v` (`docker compose down -v`) to also delete the PostgreSQL database volume. PostgreSQL is pinned to version 17 on purpose: PostgreSQL 18 moved `PGDATA` to `/var/lib/postgresql/18/docker` and its volume to `/var/lib/postgresql`, which breaks a stack that mounts the older `/var/lib/postgresql/data` path.

## Kubernetes (Module 6)

Create the local kind cluster, then build and load the application image:

```sh
kind create cluster --config k8s/kind-cluster.yaml
docker build -t devops-demo:1.0.0 .
kind load docker-image devops-demo:1.0.0 --name devops-course
```

Apply the namespace, configuration, database, application deployment, and service in order:

```sh
kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-configmap.yaml
kubectl apply -f k8s/02-secret.yaml
kubectl apply -f k8s/03-postgres.yaml
kubectl apply -f k8s/04-app-deployment.yaml
kubectl apply -f k8s/05-app-service.yaml
```

Reach the application at `http://NODE_ADDRESS:30080/api/info`.

The Kubernetes Secret is base64-encoded, not encrypted, and exists only as a teaching example.

Install metrics-server for the autoscaling lab. On kind it needs the insecure kubelet TLS argument:

```sh
helm install metrics-server metrics-server/metrics-server \
  --namespace kube-system \
  --set args={--kubelet-insecure-tls}
```

In Lab C, apply the HPA, quota, and read-only RBAC resources:

```sh
kubectl apply -f k8s/09-hpa.yaml
kubectl apply -f k8s/10-resourcequota.yaml
kubectl apply -f k8s/11-rbac-readonly.yaml
```

Render the Helm chart locally, or load the image and install the chart in its own namespace:

```sh
helm template demo charts/devops-demo
kind load docker-image devops-demo:1.0.0 --name devops-course
helm install demo charts/devops-demo -n helm-demo --create-namespace
```

Delete the Kubernetes resources and cluster when finished:

```sh
kubectl delete -f k8s/
kind delete cluster --name devops-course
```

## Ansible (Module 4)

Run the mini labs from the `ansible/` directory:

```sh
cd ansible
ansible-playbook mini-labs/02-hello.yml
ansible-playbook mini-labs/03-vars-loops.yml
ansible-playbook mini-labs/04-template-handler.yml
ansible-playbook mini-labs/05-cleanup.yml
```

The main playbook prepares the hosts with `--tags prepare` and deploys the jar built by `./mvnw clean package` with `--tags deploy`. Every `site.yml` command must include `--ask-vault-pass` because the playbook loads the encrypted vault with `vars_files`:

```sh
./mvnw clean package
cd ansible
ansible-playbook site.yml --ask-vault-pass --tags prepare
ansible-playbook site.yml --ask-vault-pass --tags deploy
```

Before creating the vault file, validate the Ansible configuration:

```sh
ansible-lint
ansible-playbook site.yml --syntax-check
```

Create the encrypted vault from the example file:

```sh
cp vault/secrets.yml.example vault/secrets.yml
ansible-vault encrypt vault/secrets.yml
```

The mini labs and ad-hoc Ansible commands do not need `--ask-vault-pass`.

Deploy another application version by overriding `app_version`:

```sh
ansible-playbook site.yml --ask-vault-pass --tags deploy -e app_version=X.Y.Z
```
