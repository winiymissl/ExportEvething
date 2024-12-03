package com.example.exportevething

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.openapi.ui.Messages
import java.awt.datatransfer.DataFlavor
import java.io.File

class ExportMenuAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // 获取当前选中的文本≤
        val selectedText = CopyPasteManager.getInstance().contents?.getTransferData(DataFlavor.stringFlavor) as? String

        if (selectedText.isNullOrEmpty()) {
            Messages.showWarningDialog("请先选择要导出的文本", "警告")
            return
        }

        // 创建文件选择器描述符
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            .withTitle("选择导出目录")

        // 显示文件选择对话框
        val selectedDirectory = FileChooser.chooseFile(descriptor, null, null)
        if (selectedDirectory != null) {
            val file = File(selectedDirectory.path, "exported_text.txt")
            // 将选中的文本保存为文件
            try {
                // 如果文件不存在，创建新文件
                if (!file.exists()) {
                    file.createNewFile()
                }
                file.writeText(selectedText)
                // 显示成功消息
                Messages.showInfoMessage("文件已保存至：${file.absolutePath}", "导出成功")
            } catch (e: Exception) {
                // 显示错误消息
                Messages.showErrorDialog("保存文件时发生错误：${e.message}" + "\n" + file.absolutePath, "导出失败")
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // 控制菜单项的可见性和启用状态
        e.presentation.isEnabledAndVisible = true
    }
}