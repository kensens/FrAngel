# FrAngel

FrAngel is a _program synthesizer_ -- a tool that automatically writes programs. Specifically, FrAngel generates Java functions from user-provided input-output examples that describe the desired function's behavior.

Disclaimer: Use FrAngel with caution. FrAngel works by generating random programs and executing them. FrAngel can cause unwanted changes to your computer (for instance by deleting files from the filesystem) if it has access to Java methods that directly or indirectly cause that behavior. The FrAngel authors are not responsible for any adverse effects caused by running FrAngel.


## Using FrAngel as a library

First, see the [FrAngel Tutorial](tutorial.md) to learn about FrAngel usage and features. The tutorial will show you how to write a short Java program that invokes the FrAngel synthesizer on your own input-output examples.

To use FrAngel as a library, include `frangel.jar` in your classpath. You will also need [Google Guava](https://github.com/google/guava) and [Gson](https://github.com/google/gson). FrAngel also expects to find `excluded.json` in the root directory of your application.

For instance, you can use the following directory structure:

    frangel_directory/
     |-- Synthesize.java
     |-- frangel.jar
     |-- excluded.json
     |-- lib/
     |  |-- gson-2.8.1.jar
     |  \-- guava-25.0-jre.jar

Some tips:
 * `Synthesize.java` is an example Java program that invokes FrAngel. You should modify it to reflect the task you want to synthesize.
 * All of these files can be found in the root directory or `lib/` subdirectory of this repository.
 * The other files in this repository are not strictly necessary to use FrAngel as a library.
 * If you have other JAR files containing definitions of classes that you wish to use during synthesis, place those JAR files in `lib/`.

Then, from `frangel_directory/`, the following commands will compile and run `Synthesize.java`:

    javac -cp ".:*:lib/*" Synthesize.java
    java -cp ".:*:lib/*" Synthesize

Note: This classpath syntax is for Linux and MacOS. For Windows, use `;` instead of `:` as the classpath separator.

If you clone this repository and make changes to FrAngel's source code (in `src/`), you can recreate `frangel.jar` with [Apache Ant](https://ant.apache.org/) by running:

    ant frangel-jar


## Running the experiments

The FrAngel paper is on [arXiv](https://arxiv.org/abs/1811.05175). All runs for the experiments in the paper can be found on [CodaLab](https://worksheets.codalab.org/worksheets/0x882075c0b92c4a2d85abdbd3d76aad78/).

To run FrAngel on all of the benchmarks, use the following:

    bash main.sh -fragments=true -angelic=true

This will take a long time to run, since there are over 100 benchmarks, each with a time limit of 30 minutes.

The strategies of "mining fragments" and "angelic conditions" can be toggled by replacing `true` with `false`. Modify `main.sh` to configure where output files are written (`-results-folder`) or the time limit in seconds (`-time`). Various other settings can be modified at `src/frangel/Settings.java`.

Our experiments used the provided `Dockerfile`, using Java 8 from Oracle.

Note: Occasionally FrAngel will randomly invoke a library method with arguments that cause that library method to infinite loop, which in turn causes FrAngel to wait forever. Under normal conditions, FrAngel will print out results for each benchmark as soon as it is solved or the timeout is reached. Thus, if 30 minutes (or the chosen time limit) have passed without progress on the benchmarks, then FrAngel must be restarted.
