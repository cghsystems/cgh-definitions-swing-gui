package net.cghsystems.definitions.client.desktop.componentes

import groovy.swing.SwingBuilder

import java.awt.BorderLayout
import javax.annotation.Resource

/**
 * @author: chris
 * @date: 22/11/2012
 */
class ResultPanel {

    @Resource(name = "stripeRenderer")
    def stripeRenderer

    @Resource(name = "swingBuilder")
    private SwingBuilder swingBuilder

    private def resultList

    def resultPanel() {
        swingBuilder.scrollPane(constraints: BorderLayout.CENTER) {
            resultList = list(name: "resultList", fixedCellWidth: 600, fixedCellHeight: 75, cellRenderer: stripeRenderer)
        }
    }

    def notifyOfDataChange(data) {
        swingBuilder.edt {
            resultList.listData = data
        }
    }
}
