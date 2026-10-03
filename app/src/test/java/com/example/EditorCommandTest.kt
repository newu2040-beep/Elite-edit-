package com.example

import com.example.data.model.ColorAdjustment
import com.example.data.model.Project
import com.example.data.model.TextLayer
import com.example.data.model.VideoClip
import com.example.domain.command.AddClipCommand
import com.example.domain.command.AddTextCommand
import com.example.domain.command.CommandHistory
import com.example.domain.command.DeleteClipCommand
import com.example.domain.command.DuplicateClipCommand
import com.example.domain.command.SplitClipCommand
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EditorCommandTest {

    @Test
    fun testAddClipAndUndo() {
        val history = CommandHistory()
        var project = Project(name = "Test Project")
        val clip = VideoClip(name = "Clip 1", uri = "uri://1", durationMs = 5000L)

        // Execute Add
        project = history.executeCommand(AddClipCommand(clip), project)
        assertEquals(1, project.clips.size)
        assertTrue(history.canUndo)
        assertFalse(history.canRedo)

        // Undo
        project = history.undo(project)!!
        assertEquals(0, project.clips.size)
        assertFalse(history.canUndo)
        assertTrue(history.canRedo)

        // Redo
        project = history.redo(project)!!
        assertEquals(1, project.clips.size)
    }

    @Test
    fun testSplitClipCommand() {
        val history = CommandHistory()
        val originalClip = VideoClip(name = "Long Clip", uri = "uri://long", durationMs = 10000L)
        var project = Project(clips = listOf(originalClip))

        val splitCmd = SplitClipCommand(clipIndex = 0, originalClip = originalClip, splitTimeWithinClipMs = 4000L)
        project = history.executeCommand(splitCmd, project)

        assertEquals(2, project.clips.size)
        assertEquals(4000L, project.clips[0].trimEndMs)
        assertEquals(4000L, project.clips[1].trimStartMs)

        // Undo split
        project = history.undo(project)!!
        assertEquals(1, project.clips.size)
        assertEquals(originalClip.id, project.clips[0].id)
    }

    @Test
    fun testAddTextLayerUndo() {
        val history = CommandHistory()
        var project = Project()
        val textLayer = TextLayer(text = "Good Vibes", fontFamilyName = "Inter")

        project = history.executeCommand(AddTextCommand(textLayer), project)
        assertEquals(1, project.textLayers.size)

        project = history.undo(project)!!
        assertEquals(0, project.textLayers.size)
    }
}
