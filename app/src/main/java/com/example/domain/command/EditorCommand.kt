package com.example.domain.command

import com.example.data.model.Project

interface EditorCommand {
    val description: String
    fun execute(current: Project): Project
    fun undo(current: Project): Project
}

class CommandHistory {
    private val undoStack = mutableListOf<EditorCommand>()
    private val redoStack = mutableListOf<EditorCommand>()

    val canUndo: Boolean get() = undoStack.isNotEmpty()
    val canRedo: Boolean get() = redoStack.isNotEmpty()

    fun executeCommand(command: EditorCommand, currentProject: Project): Project {
        val newProject = command.execute(currentProject)
        undoStack.add(command)
        redoStack.clear()
        // Keep stack reasonable size
        if (undoStack.size > 50) {
            undoStack.removeAt(0)
        }
        return newProject
    }

    fun undo(currentProject: Project): Project? {
        if (undoStack.isEmpty()) return null
        val command = undoStack.removeAt(undoStack.lastIndex)
        val revertedProject = command.undo(currentProject)
        redoStack.add(command)
        return revertedProject
    }

    fun redo(currentProject: Project): Project? {
        if (redoStack.isEmpty()) return null
        val command = redoStack.removeAt(redoStack.lastIndex)
        val reappliedProject = command.execute(currentProject)
        undoStack.add(command)
        return reappliedProject
    }

    fun clear() {
        undoStack.clear()
        redoStack.clear()
    }
}
