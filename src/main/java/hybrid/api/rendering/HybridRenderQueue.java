package hybrid.api.rendering;

import java.util.ArrayDeque;
import java.util.Queue;

public class HybridRenderQueue {

    private static final Queue<RenderTask> queue = new ArrayDeque<>();

    public static void add(RenderTask task) {
        queue.add(task);
    }

    public static void addAll(Queue<RenderTask> tasks) {
        queue.addAll(tasks);
    }

    public static void renderAll(HybridRenderer2D renderer) {
        while (!queue.isEmpty()) {
            queue.poll().render(renderer);
        }
    }

    public static Queue<RenderTask> getQueue() {
        return queue;
    }

    public static void clear() {
        queue.clear();
    }
    public static int size() {
        return queue.size();
    }
}
