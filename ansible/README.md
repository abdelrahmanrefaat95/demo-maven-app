# Ansible lab for the DevOps course

## What you need

- A control node (Ubuntu 24.04) with Ansible installed via pipx
- Two managed nodes, app-dev and app-staging (Ubuntu 24.04, user ubuntu)
- The SSH key at ~/.ssh/devops-course.pem, mode 0600

## Set up

Edit `inventory.ini` and replace `DEV_HOST_IP` and `STAGING_HOST_IP` with the real addresses, then confirm the hosts answer:

```sh
ansible-inventory --graph
ansible all -m ansible.builtin.ping
```

## The lab files

- `build.yml` teaches how automation can build the artifact on the control node before deployment. Watch every run report a change because compiling is deliberately not treated as idempotent. Give `build.yml` and `deploy.yml` the same `-e app_version` when the project version changes.
- `labs/01-first-playbook.yml` teaches the structure of a first playbook and idempotence. Watch the file task change on the first run and report `ok` on the second.
- `labs/02-variables.yml` teaches values from a play, inventory, and the command line. Watch the default release note change when you supply it with `-e`, while repeated runs stay unchanged.
- `labs/03-facts.yml` teaches how Ansible discovers host information. Watch the same facts report `ok` on both runs because displaying data changes nothing.
- `labs/04-loops.yml` teaches how one task can repeat for several items. Watch the first run install and create resources, then the second report them as already present. `ansible.builtin.apt` also accepts a list directly for one transaction instead of two; this loop is written for teaching visibility.
- `labs/05-conditions.yml` teaches how host facts and profiles decide whether work runs. Watch development skip staging work while staging runs it, with created directories becoming `ok` on the second run.
- `labs/06-register.yml` teaches how to capture a command result and select useful fields. Watch both runs remain `ok` because the read-only command is explicitly marked unchanged.
- `labs/07-template.yml` teaches how facts and variables become file content. Watch the first run update the banner and the second leave identical content unchanged.
- `labs/08-handler.yml` teaches that handlers run only when notified by a change. Watch the handler fire initially, stay quiet on the second run, and fire again after overriding the banner.
- `labs/09-cleanup.yml` teaches safe removal of the lab resources. Watch the first run remove anything present and the second remain successful with nothing left to remove.

Run one lab with:

```sh
ansible-playbook labs/01-first-playbook.yml
```

## Deploy the application

```sh
ansible-playbook build.yml
ansible-playbook deploy.yml
ansible-playbook deploy.yml --tags prepare
ansible-playbook deploy.yml --tags deploy
ansible-playbook deploy.yml --limit dev --check --diff
ansible-playbook deploy.yml -e app_version=1.1.0
```

## Going further

The `ansible-advanced/` directory shows the same work organized like a production project, with a role, YAML inventory and group variables, Ansible Vault secrets, and a rolling rollout with `serial`. It is reference material for after the course and is not part of this lab.

## Two inventory formats

Confirm that the default INI inventory and optional YAML inventory describe the same hosts and variables:

```sh
ansible-inventory -i inventory.ini --list > /tmp/ini.json
ansible-inventory -i inventory.yml --list > /tmp/yml.json
diff /tmp/ini.json /tmp/yml.json
```

Pick INI for a flat host list, and YAML once you need nesting or structured variables.
