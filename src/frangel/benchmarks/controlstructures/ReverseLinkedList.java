package frangel.benchmarks.controlstructures;

import frangel.Example;
import frangel.SynthesisTask;
import frangel.Tag;
import frangel.benchmarks.BenchmarkGroup;
import frangel.benchmarks.TaskCreator;

class LinkedListNode {
    int value;
    LinkedListNode next;
    public LinkedListNode(int value, LinkedListNode next) {
        this.value = value;
        this.next = next;
    }
    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }
    public LinkedListNode getNext() { return next; }
    public void setNext(LinkedListNode next) { this.next = next; }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListNode))
            return false;
        LinkedListNode other = (LinkedListNode) o;
        return value == other.value && (next == null ? other.next == null : next.equals(other.next));
    }
    @Override
    public String toString() {
        return value + (next == null ? "" : " -> " + next);
    }
}

public enum ReverseLinkedList implements TaskCreator {
    INSTANCE;
    static {
        BenchmarkGroup.CONTROL_STRUCTURES.register(INSTANCE);
    }

    @Override
    public SynthesisTask createTask() {
        SynthesisTask task = new SynthesisTask()
                .setName("reverseLinkedList")
                .setInputTypes(LinkedListNode.class)
                .setInputNames("node")
                .setOutputType(LinkedListNode.class)
                .addTags(Tag.WHILE);

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LinkedListNode(123, new LinkedListNode(234, new LinkedListNode(345, new LinkedListNode(456, new LinkedListNode(567, new LinkedListNode(99, null)))))) })
                .setOutput(new LinkedListNode(99, new LinkedListNode(567, new LinkedListNode(456, new LinkedListNode(345, new LinkedListNode(234, new LinkedListNode(123, null))))))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LinkedListNode(123, new LinkedListNode(234, new LinkedListNode(345, null))) })
                .setOutput(new LinkedListNode(345, new LinkedListNode(234, new LinkedListNode(123, null)))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LinkedListNode(123, new LinkedListNode(234, null)) })
                .setOutput(new LinkedListNode(234, new LinkedListNode(123, null))));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { new LinkedListNode(123, null) })
                .setOutput(new LinkedListNode(123, null)));

        task.addExample(new Example()
                .setInputs(() -> new Object[] { null })
                .setOutput(null));

        return task;
    }

    static LinkedListNode solution(LinkedListNode node) {
        LinkedListNode var = null;
        while (node != null) {
            var = new LinkedListNode(node.getValue(), var);
            node = node.getNext();
        }
        return var;
    }
}
