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
        // 获取剪贴板内容
        val transferable = CopyPasteManager.getInstance().contents
        if (transferable == null) {
            Messages.showWarningDialog("请先选择要导出的内容", "警告")
            return
        }

        // 尝试获取文本内容
        val selectedText = try {
            if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferable.getTransferData(DataFlavor.stringFlavor) as? String
            } else null
        } catch (e: Exception) {
            null
        }

        // 尝试获取文件内容
        val selectedFiles = try {
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                transferable.getTransferData(DataFlavor.javaFileListFlavor) as? List<File>
            } else null
        } catch (e: Exception) {
            null
        }

        // 如果既不是文本也不是文件，则提示错误
        if (selectedText == null && selectedFiles == null) {
            Messages.showWarningDialog("请选择文本或文件进行导出", "警告")
            return
        }

        // 创建文件选择器描述符
        val descriptor = FileChooserDescriptor(false, true, false, false, false, false)
            .withTitle("选择导出目录")

        // 显示文件选择对话框
        val selectedDirectory = FileChooser.chooseFile(descriptor, null, null)
        if (selectedDirectory != null) {
            try {
                if (selectedText != null) {
                    // 导出文本
                    val file = File(selectedDirectory.path, "exported_text.txt")
                    if (!file.exists()) {
                        file.createNewFile()
                    }
                    file.writeText(selectedText)
                    Messages.showInfoMessage("文本已保存至：${file.absolutePath}", "导出成功")
                } else if (selectedFiles != null) {
                    // 导出文件
                    selectedFiles.forEach { sourceFile ->
                        val targetFile = File(selectedDirectory.path, sourceFile.name)
                        sourceFile.copyTo(targetFile, overwrite = true)
                    }
                    Messages.showInfoMessage("文件已导出至：${selectedDirectory.path}", "导出成功")
                }
            } catch (e: Exception) {
                Messages.showErrorDialog("导出时发生错误：${e.message}", "导出失败")
            }
        }
    }

    override fun update(e: AnActionEvent) {
        // 始终显示在右键菜单中
        e.presentation.isEnabledAndVisible = true
        // 设置为上下文菜单项
        e.presentation.setVisible(true)
    }
}