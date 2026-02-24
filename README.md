# GitHub User Activity CLI

A command-line tool that fetches and displays the recent public activity of a GitHub user, built with plain Java 17 and no external dependencies.

> Project from [roadmap.sh](https://roadmap.sh/projects/github-user-activity)

## Requirements

- Java 17+
- Maven 3.6+

## Build

```bash
mvn clean package
```

The jar is output to `target/github-user-activity-1.0-SNAPSHOT.jar`.

## Usage

```bash
github-activity <username>                    # Fetch recent activity
github-activity <username> --filter <type>    # Filter by event type
github-activity --language <en|vi>            # Switch display language
github-activity --help                        # Show help
```

**Short flags:** `-f` for `--filter`, `-l` for `--language`, `-h` for `--help`

### Event types for `--filter`

| Value | Description |
|---|---|
| `push` | Push commits |
| `watch` | Star a repository |
| `fork` | Fork a repository |
| `create` | Create branch/tag/repo |
| `delete` | Delete branch/tag |
| `issues` | Open/close issues |
| `pullrequest` | Open/close pull requests |
| `release` | Publish a release |
| `issuecomment` | Comment on an issue |
| `commitcomment` | Comment on a commit |

### Examples

```bash
github-activity kamranahmedse
github-activity kamranahmedse --filter push
github-activity kamranahmedse -f watch
github-activity --language vi
```

### Sample output

```
- Pushed 3 commits to kamranahmedse/developer-roadmap
- Starred nickvdyck/webbundlr
- Opened a pull request in kamranahmedse/roadmap.sh
- Published a release in kamranahmedse/mondex
```

## Error handling

| Scenario | Message |
|---|---|
| User not found | `Error: User 'xyz' not found.` |
| Rate limit hit | `Error: GitHub API rate limit exceeded.` |
| No internet | `Error: Could not connect to GitHub API.` |

## Project structure

```
src/main/java/org/de013/githubuseractivity/
├── Main.java
├── cli/
│   ├── CommandParser.java
│   └── command/
│       ├── Command.java
│       ├── DefaultCommand.java
│       ├── HelpCommand.java
│       └── LanguageCommand.java
├── service/
│   ├── UserActivityService.java
│   └── UserActivityServiceImpl.java
└── util/
    ├── EventFormatter.java
    └── Messages.java
```
