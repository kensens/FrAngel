# FrAngel Tutorial

FrAngel is a _program synthesizer_ -- a tool that automatically writes programs. Specifically, FrAngel generates Java functions from user-provided examples that describe the desired function's behavior. In this tutorial, you will use FrAngel to synthesize a simple function.

Disclaimer: Use FrAngel with caution. FrAngel works by generating random programs and executing them. FrAngel can cause unwanted changes to your computer (for instance by deleting files from the filesystem) if it has access to Java methods that directly or indirectly cause that behavior. The FrAngel authors are not responsible for any adverse effects caused by running FrAngel.

## Preliminaries

Before we dive into FrAngel, we cover `Class` objects and lambda expressions, two important features of Java that are necessary to use FrAngel effectively. If you are familiar with these features, then this section can be skipped.

#### Class Objects

To specify types (for instance, the input and output types), FrAngel uses [`java.lang.Class`](https://docs.oracle.com/javase/8/docs/api/java/lang/Class.html) objects. To obtain a `Class` object representing a particular type, simply write the type normally (as if declaring a variable of that type), and append `.class`. Here are some examples:

* `int.class`
* `double[].class`
* `String.class`
* `List.class`
* `Point.class`
* `Line2D.Double.class` (`Line2D.Double` is a nested class inside `Line2D`)
* `void.class` (used to denote that a function does not return a value)

Note: due to Java's implementation of generics ([type erasure](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html)), `List<String>.class` is not valid. The `Class` object corresponding to a `List<String>` object is simply `List.class`. FrAngel does not fully support generics.

#### Lambda Expressions

Lambda expressions are like functions that can be treated as objects and passed as arguments. Lambda expressions are used by FrAngel in various scenarios:

1. Given no arguments, return an `Object[]` representing example inputs
2. Given two arguments, return a `boolean` representing whether the arguments should be considered equal
3. Given one argument, return a `String` representation of the argument

For example, the following are lambda expressions: 

    // Produces an Object[] containing inputs (scenario 1)
    () -> new Object[] { new Point(11, 20), new Point(15, -50) }

    // Tests whether two Point objects are equal (scenario 2)
    (Point p, Point q) -> p.x == q.x && p.y == q.y

    // Creates a String representation of a Point (scenario 3)
    (Point p) -> "(" + p.x + ", " + p.y + ")"

The last part of a lambda expression (after the `->`) can span multiple lines. For instance, this is a lambda expression for scenario 1 that requires a few lines to set up the inputs:

    () -> {
        List<Object> list = new ArrayList<>();
        list.add("a");
        list.add(1);
        return new Object[] { list };
    }

Method references can be used in place of lambda expressions. Suppose the following class and method exist:

    class ContainingClass {
        static boolean pointEquality(Point p, Point q) {
            return p.x == q.x && p.y == q.y;
        }
    }

Then, the method reference `ContainingClass::pointEquality` is equivalent to the lambda expression for scenario 2 above. This is more readable and avoids code duplication when the lambda expression is already implemented elsewhere as a method.

## Task

In this tutorial we will use FrAngel to synthesize the following function (using the [`java.awt.Point`](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html) class):

    // Returns the element-wise sum of two Points without modifying the inputs.
    static Point addPoints(Point p1, Point p2) {
        ...
    }

For example, if the two input points are `(11, 20)` and `(15, -50)`, then `addPoints` should return the point `(26, -30)`.

## Starter Code

Using the function signature and the example given, the following starter code shows how to run FrAngel.

    import frangel.*;
    import java.awt.Point;

    public class TutorialStarter {
        public static void main(String[] args) {

            // Step 1: Create a SynthesisTask describing the desired function.
            SynthesisTask task = new SynthesisTask()
                    .setName("addPoints")
                    .setInputTypes(Point.class, Point.class)
                    .setInputNames("p1", "p2")
                    .setOutputType(Point.class);

            // Step 2: Add Example objects to the SynthesisTask.
            task.addExample(new Example()
                    .setInputs(() -> new Object[] { new Point(11, 20), new Point(15, -50) })
                    .setOutput(new Point(26, -30)));

            // More examples can be added by calling addExample() again:
            // task.addExample(...);

            // Step 3: Run the synthesizer.
            FrAngel.synthesize(task);
        }
    }

Step 1 uses the function signature of `addPoints` to set up a `SynthesisTask`. The input and output types are specified with `Class` objects.

Step 2 adds `Example` objects to the `SynthesisTask`. An `Example` includes values for the inputs to `addPoints` and the corresponding output value. The starter code simply uses the example given in the task description above. Note that `setInputs()` takes a lambda expression that accepts no arguments and returns an `Object[]` containing the desired inputs. This allows FrAngel to obtain fresh copies of the inputs.

Step 3 invokes the FrAngel synthesizer. FrAngel will run until it finds a function that adheres to the given examples, and the result will be printed to the console.

Running the starter code, FrAngel thinks for about 5 seconds (on average) and outputs something like:

    static java.awt.Point addPoints(java.awt.Point p1, java.awt.Point p2) {
        p1.translate(p2.x, p2.y);
        return p1;
    }

(FrAngel is a randomized algorithm, so it does not always produce the same result.)

## Making Inputs Immutable

The starter code's output (above) looks right at first glance, but in fact the `translate` method (defined in the [`Point`](https://docs.oracle.com/javase/8/docs/api/java/awt/Point.html#translate-int-int-) class) modifies `p1`, but we wanted to return a new `Point` without modifying the inputs.

**Exercise: Fix this in the starter code by calling `task.makeInputsImmutable()` as follows:**

<pre>SynthesisTask task = new SynthesisTask()
        .setName("addPoints")
        .setInputTypes(Point.class, Point.class)
        .setInputNames("p1", "p2")
        .setOutputType(Point.class)
        <b>.makeInputsImmutable();</b></pre>

With this option, FrAngel requires that each input is equal before and after execution of `addPoints`. After this change, FrAngel takes much longer to find a solution, about 3-4 minutes on average (you can proceed to the next section without waiting). The slowdown occurs because FrAngel now rejects the above `translate` solution, since it modifies one of the input points. The intended solution (which does not modify the input points) is harder to find.

## Special Cases

We can help FrAngel by providing examples for _special cases_. Consider the following examples:

       p1         p2            output
    1. (11, 20) + (15, -50) --> (26, -30)
    2. (11, 0)  + (15, 0)   --> (26, 0)
    3. (0, 20)  + (0, -50)  --> (0, -30)

The first example (the same one as before) is "general" while the second and third are "special cases" (where only the x- or y-coordinate is summed). These special cases help FrAngel learn from partially-correct implementations. For instance, Example 2 helps FrAngel learn to sum x-coordinates without requiring correct behavior for the y-coordinates. That knowledge acts as a stepping-stone for the general task.

A key aspect of FrAngel is learning from simpler variations of the general task. FrAngel learns about these simpler variations through special case examples. When creating such examples, it helps to think about various aspects of the problem:

* Does the problem decompose into smaller tasks, as in `addPoints`? If so, consider inputs that only involve one subtask.
* Are there edge cases, degenerate inputs, or other inputs with a particular structure? These kinds of inputs often simplify the problem somehow.
* If the task involves a loop, it helps to include different inputs that require 0, 1, 2, and many (~5) loop iterations.
* If the task involves an `if` statement, it helps to include an input where the `if` statement condition evaluates to `false`, and another input where the condition evaluates to `true`.

Special-case examples can typically be created by modifying or simplifying an existing general example. For instance, the special case examples for `addPoints` above are obtained by replacing parts of the general example with `0`.

**Exercise: Create `Example` objects for the two new examples above, and remember to add them to the `SynthesisTask`.**

FrAngel will now output programs that satisfy subsets of examples as they are found, which can help us keep track of its progress in solving the special cases.

At this point, FrAngel should quickly find a correct implementation of `addPoints`! With all 3 examples above, FrAngel takes about 10 seconds on average to find a solution.

## Unit Testing

FrAngel can also serve as a unit-testing framework, using the `SynthesisTask` and `Example` objects created earlier. Add the following _buggy_ `addPoints` implementation to the `TutorialStarter` class (or any other class), outside the `main` method:

    static Point addPointsBuggy(Point p1, Point p2) {
        return new Point(p1.x + p2.x, p1.y + p1.y);
    }

Then, comment out the line `FrAngel.synthesize(task);` and run the unit tests instead:

    // FrAngel.synthesize(task);
    FrAngel.test(task, TutorialStarter.class, "addPointsBuggy");

The first argument to `FrAngel.test()` is the `SynthesisTask` constructed earlier, which contains the `Example` objects. The second argument is a `Class` object for the class containing the implementation to be tested, and the third argument is the method name.

FrAngel outputs the values of various objects when an `Example` (test case) fails. Currently a `Point` (11, 20) is printed as `java.awt.Point[x=11,y=20]`, using the `Point.toString()` method by default. We can customize how `Point` objects are printed by modifying the `SynthesisTask`:

    task.addToString(Point.class, (Point p) -> "(" + p.x + ", " + p.y + ")");

The second argument is a lambda expression that takes a single argument and returns a `String` representation of that argument. (This does not need to handle `null` values.)

Does FrAngel's unit testing output help pinpoint the bug in `addPointsBuggy`?

**Exercise: Fix the bug so that the tests pass.**

## More Features

Some important features of FrAngel were not used in the `addPoints` problem, so we discuss them here.

#### Custom Equality Testers

FrAngel often needs to compare two objects for equality, for instance when checking if a potential implementation produces an output that matches the expected output as given in the `Example`. FrAngel uses the objects' `.equals()` method for equality comparisons. Some classes unfortunately do not implement this method in a desirable way. Recall that we can customize how FrAngel converts an object to a `String` with `task.addToString`; similarly, we can customize object equality testing as follows:

    task.addEqualityTester(Point.class, (Point p, Point q) -> p.x == q.x && p.y == q.y);

The second argument is a lambda expression that takes two arguments and returns `true` if they are "equal" and `false` otherwise. (This does not need to handle `null` values.)

#### Modifying Inputs

Recall that we used `task.makeInputsImmutable();` to disallow modifying the inputs for `addPoints`. However, sometimes we do want to modify an argument as part of the desired behavior. For instance, suppose we want to implement `addPoints2` that modifies the first argument:

    // Modifies p1 by adding p2. Does not modify p2.
    static void addPoints2(Point p1, Point p2) {
        ...
    }

First, we change the output type to `void.class`. Then, this behavior can be encoded into the `Example` objects as follows:

    task.addExample(new Example()
            .setInputs(() -> new Object[] { new Point(11, 20), new Point(15, -50) })
            .setModifiedInput(1, new Point(26, -30)));

No output is given because `addPoints2` does not return a value. Instead, we want to modify input 1 (namely, `p1`) so that it is equal to the specified value. Setting a modified input overrides the `task.makeInputsImmutable()` behavior, but only for that `Example` and that input. If we want multiple inputs to be modified, we can call `setModifiedInput` multiple times on the same `Example` for different inputs.

#### Using Other Classes

FrAngel automatically uses methods, constructors, and fields from the classes (and superclasses) in the given function signature. To allow FrAngel to use other classes, additional classes and entire packages can be added to the `SynthesisTask`:

    task.addClasses(Math.class, Collections.class, MyOtherClass.class);
    task.addPackages("java.awt.geom", "some.custom.package");

Beware: adding too many packages can hinder FrAngel's efficiency.

#### Adding Literals

FrAngel automatically uses the default literals `-1`, `0`, `1`, `2`, `0.0`, `1.0`, `2.0`, `true`, `false`, `""`, and `null`. To add more literals, use the following:

    // Only primitive and String literals supported
    task.addLiterals(String.class, "myLiteral1", "myLiteral2", "etc.");
    task.addLiterals(int.class, 123, 456);

#### Limited Generics Support

FrAngel has limited support for generics: it can only associate one parameter type for each generic type. For example:

* Associating the `List` generic type with the `String` parameter type means that _all_ `List` objects will be treated as `List<String>`.
* Associating the `Map` generic type with the `Integer` parameter type means that _all_ `Map` objects will be treated as `Map<Integer, Integer>`.

Unfortunately, this means FrAngel cannot reason about `OtherClass<TypeX, TypeY>`, or about `SomeClass<TypeX>` and `SomeClass<TypeY>` in the same program. To associate a parameter type for a generic type:

    task.addGenerics(GenericType.class, ParameterType.class);

## Usage Tips

Here are some tips about using FrAngel:

* While creating new examples for a synthesis task, you can run FrAngel on the existing examples at the same time. FrAngel may be able to find a solution before the new examples are finished. Remember to stop any existing instances of FrAngel before running a new one.
* Ensure that your examples are correct. Examine FrAngel's progress -- if FrAngel finds a program that solves all examples except one, then the missing example might have a mistake.
* Sometimes FrAngel produces a strange or unintuitive program. Try running FrAngel again, and a better solution might be found.
* FrAngel's unit testing feature can help you improve the programs produced by FrAngel. Copy FrAngel's solution into your code and run the unit tests, which should pass. Then, make modifications to the program to see if any tests break.

## Limitations

FrAngel currently cannot handle/use the following:

* Throwing exceptions
* `else`, `break`, `continue`, `switch`, and early `return`
* Recursion
* Functions with side-effects like writing a file to disk
* Autoboxing, unboxing, and casting
* Bit twiddling operators
* Full Java generics
* Classes and methods in the "excluded.json" file

Furthermore, FrAngel has difficulty with the following:

* Long functions (more than 4-5 lines)
* Functions that return a `boolean` or integer
* Using local variables, especially multiple ones for the same task

