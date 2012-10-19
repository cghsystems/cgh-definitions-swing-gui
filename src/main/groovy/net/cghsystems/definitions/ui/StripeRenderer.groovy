package net.cghsystems.definitions.ui

import java.awt.Color
import java.awt.Component

import javax.annotation.Resource
import javax.swing.BorderFactory as BF
import javax.swing.BoxLayout
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class StripeRenderer extends DefaultListCellRenderer {

    def listComponents = [:]

    @Resource(name = "swingBuilder")
    final builder

    def currentlySelected

    @Override
    Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

        def box = listComponents.index
        def colour = {
            index % 2 == 0 ? new Color(230,230,255) : Color.WHITE
        }

        def textBox = { def text ->
            builder.textField(text: text, opaque: true, enabled: true, border: BF.createEmptyBorder(), background: colour())
        }

        if(!box) {
            box = builder.panel(background: colour()) {
                boxLayout(axis:BoxLayout.Y_AXIS)
                lineBorder(color: colour(), thickness: 5)
                textBox("Name:  ${value.name}")
                textBox("Defintion:  ${value.definition}")
                textBox("Description:  ${value.description}")

                listComponents.put(index,this)
            }
        }

        if(isSelected) {
            box.setBorder(BF.createLineBorder(Color.BLUE, 1))
            currentlySelected = value
        }

        return box
    }
}