import com.anvarzhonzhurajev.taskmanager.managers.*
import com.anvarzhonzhurajev.taskmanager.processes.Process
import com.anvarzhonzhurajev.taskmanager.processes.LoudProcess
import com.anvarzhonzhurajev.taskmanager.processes.QuietProcess
import kotlin.math.ceil
import kotlin.math.floor

const val DEFAULT_MAX_CAPACITY = 5

/**
 * This app demonstrates usage of TaskManager component and its derivatives
 *
 * By default, task managers have capacity of 5 processes.
 * You can change this by providing `--capacity=` switch at startup
 */
fun main(args: Array<String>) {
    val capacityParameter = args.find { it.startsWith("--capacity=")}
    val capacity = try {
        Integer.parseInt(capacityParameter?.replace("--capacity=", ""))
    } catch (e: Exception) {
        DEFAULT_MAX_CAPACITY
    }

    // Demo for adding processes within manager's capacity and listing
    var taskManager = TaskManager(20)
    initializeTaskManagerUpToCapacity(taskManager)
    println("TaskManager contents after reaching capacity")
    printProcessList(taskManager.list(SortingOptions.OLDEST), "Oldest to newest")
    printProcessList(taskManager.list(SortingOptions.PRIORITY_ASC), "Least to most prioritized")
    printProcessList(taskManager.list(SortingOptions.NEWEST), "Newest to oldest")
    printProcessList(taskManager.list(SortingOptions.PRIORITY_DESC), "Most to least prioritized")


    // Demo for killing all the tasks
    taskManager.killAll()
    println("Task Manager after killing all processes:")
    printProcessList(taskManager.list(), "Natural ordering")


    // Demo for killing the process / the group of processes
    taskManager = TaskManager(20)
    for (i in 0 until taskManager.capacity) {
        addRandomTask(taskManager, 2)
    }
    println("Task Manager after filling up with 20 processes of priorities 1 or 2:")
    printProcessList(taskManager.list(), "Natural ordering")
    // kill all processes with prio 1 (we hope that random() works ok on sample of 20)
    taskManager.killGroup(1)
    println("Task Manager after killing all processes of priority 1:")
    printProcessList(taskManager.list(), "Natural ordering")
    // add one more task just in case if all processes were deleted on previous step
    addRandomTask(taskManager, 2)
    // kill the first process, now we are sure it exists
    val idToDelete = taskManager.taskList[0].id
    println("Task Manager before killing the process with id=$idToDelete")
    printProcessList(taskManager.list(), "Natural ordering")
    taskManager.killById(idToDelete)
    println("Task Manager after killing the process with id=$idToDelete")
    printProcessList(taskManager.list(), "Natural ordering")


    // Demo of adding tasks for all three variations of TaskManager
    listOf(TaskManager(capacity), QueuedTaskManager(capacity), PriorityTaskManager(capacity)).forEach {
        println("\nDemo for " + it.javaClass.simpleName)

        // fill task manager up to its capacity
        initializeTaskManagerUpToCapacity(it)
        println("Task Manager when capacity has been reached:")
        printProcessList(it.list(), "Natural ordering")

        // add another task (behaves differently for different classes)
        val processToAdd = addRandomTask(it)
        println("Attempted to add process $processToAdd")
        println("Task Manager after adding another process:")
        printProcessList(it.list(), "Natural ordering")
    }
}

private fun printProcessList(tasks: List<Task>, ordering: String) {
    println("Tasks, $ordering")
    println(String.format("%-8s", "id") + " " + String.format("%-8s", "priority") + " process")
    tasks.forEach { println(String.format("%-8d %-8d %s", it.id, it.priority, it.process.javaClass.simpleName)) }
}

private fun initializeTaskManagerUpToCapacity(taskManager: TaskManager, maxPriority: Int = 5) {
    for (i in taskManager.taskList.size until taskManager.capacity) {
        addRandomTask(taskManager,  maxPriority)
    }
}

private fun addRandomTask(taskManager: TaskManager, maxPriority: Int = 5): Pair<Int, Process> {
    val priority = ceil(maxPriority * Math.random()).toInt()
    val process = if (floor(2 * Math.random()) == 0.0) LoudProcess() else QuietProcess()
    taskManager.add(priority, process)
    return Pair(priority, process)
}