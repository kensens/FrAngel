// Contains constants and other settings.

package frangel;

import java.util.*;

import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;
import frangel.benchmarks.controlstructures.*;
import frangel.benchmarks.geometry.*;
import frangel.benchmarks.github.*;
import frangel.benchmarks.sypet.*;

@SuppressWarnings("unused") // unused imports allow changing TASK_CREATOR quickly
public class Settings {

    ////////////////////
    // Console Output //
    ////////////////////

    // VERBOSE levels
    // 0: Print results only (for experiments)
    // 1: Print fragment programs (for interactive use)
    // 2: Print progress periodically
    // 3: Print all programs evaluated
    // 4: Print angelic evaluation
    public static int VERBOSE = 0;
    public static final int PROGRESS_DELAY = 10; // Seconds between progress output at VERBOSE >= 1
    public static final boolean LOG_TIMING = false;
    public static boolean CONSOLE_COLORS = false;


    ////////////////////////
    // Task Specification //
    ////////////////////////

    public static boolean RUN_ALL_GROUPS = true;
    public static boolean RUN_ALL_IN_GROUP = false;
    public static TaskCreator TASK_CREATOR = null;
    public static SynthesisTask TASK = (TASK_CREATOR == null ? null : TASK_CREATOR.createTask().setGroup(BenchmarkGroup.findGroup(TASK_CREATOR).toString()));
    public static BenchmarkGroup BENCHMARK_GROUP = BenchmarkGroup.CONTROL_STRUCTURES;

    public static boolean CHECK_SOLUTIONS = false;
    public static boolean COUNT_EXAMPLES_AND_COMPONENTS = false;


    ////////////////////////
    // Algorithm Settings //
    ////////////////////////

    public static int TIME_LIMIT = 1800; // seconds
    public static boolean MINE_FRAGMENTS = true;
    public static boolean USE_ANGELIC_CONDITIONS = true;
    public static double SIMPLIFICATION_TIME = 0.1; // fraction of synthesis time
    public static boolean USE_CLASS_CONSTANTS = true;


    //////////////////////////
    // For SyPet Benchmarks //
    //////////////////////////

    /*
     *  SyPet mode:
     *     - turns off control structures, operator and literal expressions, and fields
     *     - turns on some extra runtime argument checks (see Interpreter.java)
     *     - turns on using the hardcoded polymorphism map
     */
    public static boolean SYPET_MODE = false; // Set in Main.java
    public static final boolean HARDCODE_POLYMORPHISM = true; // Hardcode (as in SyPet) specific polymorphism rules
    @SuppressWarnings("serial")
    public static final Map<Class<?>, Set<Class<?>>> POLYMORPHISM_MAP = new HashMap<Class<?>, Set<Class<?>>>() {{ // from SyPet's CONFIG.json
        put(java.awt.Shape.class, new HashSet<>(Arrays.asList(java.awt.geom.Ellipse2D.class, java.awt.geom.Rectangle2D.class)));
        put(org.joda.time.ReadablePartial.class, new HashSet<>(Arrays.asList(org.joda.time.LocalDate.class)));
        put(org.joda.time.ReadableInstant.class, new HashSet<>(Arrays.asList(org.joda.time.DateTime.class)));
        put(org.w3c.dom.Node.class, new HashSet<>(Arrays.asList(org.w3c.dom.Element.class)));
        put(org.jsoup.nodes.Element.class, new HashSet<>(Arrays.asList(org.jsoup.nodes.Document.class)));
        put(java.io.Reader.class, new HashSet<>(Arrays.asList(java.io.StringReader.class)));
        put(org.joda.time.base.AbstractDateTime.class, new HashSet<>(Arrays.asList(org.joda.time.DateTime.class)));
        put(java.lang.Object.class, new HashSet<>(Arrays.asList(org.w3c.dom.Document.class)));
        put(org.joda.time.field.AbstractReadableInstantFieldProperty.class, new HashSet<>(Arrays.asList(org.joda.time.DateTime.Property.class)));
        put(org.apache.commons.math.linear.SingularValueDecomposition.class, new HashSet<>(Arrays.asList(org.apache.commons.math.linear.SingularValueDecompositionImpl.class)));
    }};


    /////////////////////////
    // Directory Structure //
    /////////////////////////

    public static int RUN = -1;
    public static String RESULTS_FOLDER = "results/json/";
    public static final String EXCLUDED_JSON_FILE = "excluded.json";


    ///////////////////////
    // Program Execution //
    ///////////////////////

    // Bounds time during program execution
    public static final int MAX_LOOP_ITERATIONS = 100; // Max number of total loop iterations (across all loops)
    public static final int MAX_SINGLE_LOOP_ITERATIONS = 25; // Max number of consecutive iterations of a single loop

    // Bounds memory usage during program execution
    public static final int MAX_STRING_LEN = 100; // Squares memory usage: str.replace("", str)
    public static final int MAX_COLLECTION_SIZE = 100; // Doubles memory usage: list.addAll(list) and list.add(list.toString())
    public static final int MAX_ARRAY_LEN = 100;

    // Evaluation of angelic programs
    public static final int NUM_ANGELIC_CODE_PATHS = 55;
    public static final double SKIP_DUPLICATE_ANGELIC_PROB = 0.75;
    public static final double FRACTION_SMALL = 0.75 - 0.00001;

    // Resolving angelic conditions
    public static final double MIN_RESOLVE_CONDITIONS_SEC = 0.2;
    public static final double MAX_RESOLVE_CONDITIONS_SEC = 10.0;
    public static final int RESOLVE_CONDITION_STRICTNESS = 0;


    ////////////////////////
    // Program Generation //
    ////////////////////////

    // Various size bounds during random program generation
    public static final int MIN_SIZE = 5; // MIN_SIZE <= (size of program) <= MAX_SIZE
    public static final int MAX_SIZE = 40;
    public static final int MAX_BLOCK_SIZE = 4; // 1 <= (# statements in a block) <= MAX_BLOCK_SIZE
    public static final int MAX_LOCAL_VARS = 2; // 1 <= (# local variables) <= MAX_LOCAL_VARS
    public static final int MIN_LOOP_COND_SIZE = 1; // MIN_LOOP_COND_SIZE <= (loop condition size) <= MAX_LOOP_COND_SIZE
    public static final int MAX_LOOP_COND_SIZE = 15;
    public static final int MIN_IF_COND_SIZE = 1; // MIN_IF_COND_SIZE <= (if condition size) <= MAX_IF_COND_SIZE
    public static final int MAX_IF_COND_SIZE = 15;
    public static final int MAX_ANGELIC_CONDITIONS = 4;
    public static final int MAX_LINE_SIZE = 15; // applies to single-line statements, control structure conditions, and the return expression

    // Failures during program generation before giving up
    public static final int GEN_RETURN_TRIES = 5;
    public static final int GEN_FUNCTION_TRIES = 1; // Potentially exponential-time if >= 2

    // When splitting sizes, the minimum size to allocate to a given expression / statement
    public static final int MIN_EXP_SIZE = 1;
    public static final int MIN_STATEMENT_SIZE = 8;

    // Generating code similar to a fragment
    public static final double PROB_REPLACE_WITH_LOOPVAR = 0.25;
    public static final double GEN_SIMILAR_PROB_NEW = 0.25;
    public static final int SIMILAR_NEW_EXTRA_SIZE = 8; // Without this, fragments wouldn't ever "grow"

    // Generating expressions to replace angelic conditions
    public static final int MAX_RESOLVE_COND_SIZE = 20;

    // Maximum number of programs to remember (for duplicate-checking), about 200 MB per million elements
    public static final int MAX_ANGELIC_SET_SIZE = 5 * 1000*1000;
    public static final int MAX_NON_ANGELIC_SET_SIZE = 5 * 1000*1000;

    // Style of output programs
    public static final String INDENT = "    ";
    public static boolean USE_SIMPLE_NAME = false; // "Point2D.Double" (true) or "java.awt.geom.Point2D.Double" (false)
}
