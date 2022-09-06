package managers;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomLinkedList {
    private final HashMap<Integer, Node> mapToList = new HashMap<>();
    private Node nodeHead = null;

    public void addLastTask(Task task) {
        removeTask(task.toInt());
        if (nodeHead == null)
            nodeHead = new Node(task, null, null);
        else
            nodeHead = nodeHead.addNode(task);

        mapToList.put(task.toInt(), nodeHead);
    }

    private void removeNode(Node node) {
        node.removeNode();
    }

    public boolean removeTask(int id) {
        Node nd = mapToList.get(id);
        if (nd != null) {
            if (nd == nodeHead) nodeHead = nodeHead.prev;
            removeNode(nd);
            mapToList.remove(id);

            return true;
        }
        return false;
    }

    public List<Task> getHistory() {
        ArrayList<Task> out = new ArrayList<>();
        Node nd = nodeHead;
        while (nd != null) {
            out.add(nd.value);
            nd = nd.prev;
        }
        return out;
    }

    private static class Node {
        private final Task value;
        private Node prev;
        private Node next;

        Node(Task value, Node prev, Node next) {
            this.value = value;
            this.prev = prev;
            this.next = next;
        }

        public Node addNode(Task value) {
            Node newNode = new Node(value, this, this.next);
            if (this.next != null) this.next.prev = newNode;
            this.next = newNode;
            return newNode;
        }

        public void removeNode() {
            if (next != null)
                next.prev = prev;
            if (prev != null)
                prev.next = next;
        }
    }
}