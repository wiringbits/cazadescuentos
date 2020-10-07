# Contributing

This is an incomplete list of things to take care while contributing.


## Commits
- Every commit message should have this format `$project: title` being `$project` the root project's directory (like `pwa`, `landing-page`, etc), see the [commit list](https://github.com/wiringbits/cazadescuentos/commits/master) to get a better idea, also, the message should be meaningful and describe what is changed.
- Don't touch files or pieces non-related to the commit message, create a different commit instead.
- Keep the commits simple to make the reviews easy.
- Merge commits will be rejected, use rebase instead, run `git config pull.rebase true` after cloning the repository to rebase automatically.
- Every commit should have working code with all tests passing.
- Every commit should include tests unless it is not practical.

## Code style
- We use [scalafmt](https://scalameta.org/scalafmt/) to format the code automatically, follow the [IntelliJ setup for scalafmt](https://scalameta.org/scalafmt/docs/installation.html#intellij).

## Pull requests
- The pull requests should go to the `master` branch.

## Other
- `master` branch should never be broken, it contains the current version running in production.



## Environment
It is simpler to use the recommended developer environment:
- IntelliJ with the Scala plugin.
