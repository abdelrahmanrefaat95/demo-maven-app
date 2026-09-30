# Ansible deployment organized as a role

## What this is

This folder contains the same playbook as `ansible/`, reorganized as a role so the two layouts can be compared side by side.

| Part of `ansible/deploy.yml` | Home in this folder |
| --- | --- |
| Play 1 variables | `roles/app/defaults/main.yml` |
| Play 1 tasks | `roles/app/tasks/main.yml` |
| Play 1 handler | `roles/app/handlers/main.yml` |
| Play 1 templates | `roles/app/templates/` |
| Play 1 wrapper and Play 2 verification | `site.yml` |

A role is worth the extra files when automation will be reused, shared, or expanded; a small one-off playbook is usually clearer without that structure.

## Two inventory formats

Confirm that the default INI inventory and optional YAML inventory describe the same hosts and variables:

```sh
ansible-inventory -i inventory.ini --list > /tmp/ini.json
ansible-inventory -i inventory.yml --list > /tmp/yml.json
diff /tmp/ini.json /tmp/yml.json
```

Pick INI for a flat host list, and YAML once you need nesting or structured variables.
