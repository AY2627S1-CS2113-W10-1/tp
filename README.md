# TripJio

TripJio is a command-line budget tracker and travel app for students. It records personal,
shared and foreign-currency expenses, checks monthly and trip budgets, and reports spending
by month, trip and category. All data is stored locally in a plain text file.

Useful links:

* [User Guide](https://ay2627s1-cs2113-w10-1.github.io/tp/UserGuide.html)
* [Developer Guide](https://ay2627s1-cs2113-w10-1.github.io/tp/DeveloperGuide.html)
* [About Us](https://ay2627s1-cs2113-w10-1.github.io/tp/AboutUs.html)

## Running TripJio

Prerequisites: JDK 25.

1. Download the latest `tripjio.jar` from the [Releases](https://github.com/AY2627S1-CS2113-W10-1/tp/releases) page.
1. Copy it into an empty folder, open a terminal there and run `java -jar tripjio.jar`.
1. Type `help` to see all commands. Data is saved in `data/tripjio.txt` in the same folder.

## Setting up in IntelliJ

Prerequisites: JDK 25 (use the exact version), update IntelliJ to the most recent version.

1. **Ensure IntelliJ JDK 25 is defined as an SDK**, as described [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk) -- this step is not needed if you have used JDK 25 in a previous IntelliJ project.
1. **Import the project _as a Gradle project_**, as described [here](https://se-education.org/guides/tutorials/intellijImportGradleProject.html).
1. **Verify the setup**: After the importing is complete, locate the `src/main/java/seedu/tripjio/TripJio.java` file, right-click it, and choose `Run 'TripJio.main()'`. If the setup is correct, you should see something like the below:
   ```
   ████████╗██████╗ ██╗██████╗     ██╗██╗ ██████╗
   ╚══██╔══╝██╔══██╗██║██╔══██╗    ██║██║██╔═══██╗
      ██║   ██████╔╝██║██████╔╝    ██║██║██║   ██║
      ██║   ██╔══██╗██║██╔═══╝██   ██║██║██║   ██║
      ██║   ██║  ██║██║██║    ╚█████╔╝██║╚██████╔╝
      ╚═╝   ╚═╝  ╚═╝╚═╝╚═╝     ╚════╝ ╚═╝ ╚═════╝
   
   Hello! Welcome to TripJio, your travel budget buddy.
   What would you like to do?
   ```
   Type `exit` to quit.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Build automation using Gradle

* This project uses Gradle for build automation and dependency management (see `build.gradle`).
* `./gradlew run` (`.\gradlew.bat run` on Windows) runs the app with assertions enabled.
* `./gradlew shadowJar` builds the executable JAR at `build/libs/tripjio.jar`.
* If you are new to Gradle, refer to the [Gradle Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/gradle.html).

## Testing

### I/O redirection tests

* Run `./gradlew test --tests '*TextUiTest'` (`.\gradlew.bat` on Windows) to test the packaged CLI
  against the checked-in input/output fixtures, including a restart with saved data.
* These tests also run automatically in `./gradlew check` and the existing GitHub Actions jobs.
  See [the Text UI test instructions](text-ui-test/README.md) for fixture coverage and maintenance.

### JUnit tests

* JUnit tests are in `src/test/java/seedu/tripjio`, mirroring the packages in `src/main/java`.
* Run `./gradlew test` to run all of them.
* If you are new to JUnit, refer to the [JUnit Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/junit.html).

## Checkstyle

* The Checkstyle rules are in `config/checkstyle`. Run `./gradlew check` to run Checkstyle together with all tests.
* If you are new to Checkstyle, refer to the [Checkstyle Tutorial at se-education.org/guides](https://se-education.org/guides/tutorials/checkstyle.html).

## CI using GitHub Actions

The project uses [GitHub actions](https://github.com/features/actions) for CI. When you push a commit to this repo or PR against it, GitHub actions will run `./gradlew check` on Ubuntu, macOS and Windows.

## Documentation

The `/docs` folder contains the User Guide, Developer Guide and team pages. They are published
with GitHub Pages from the `master` branch `/docs` folder at
<https://ay2627s1-cs2113-w10-1.github.io/tp/>.
