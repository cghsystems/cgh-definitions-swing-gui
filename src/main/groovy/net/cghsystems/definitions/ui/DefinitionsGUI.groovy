package net.cghsystems.definitions.ui


import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j

import java.awt.BorderLayout as BL
import java.awt.Color
import java.awt.FlowLayout
import java.awt.SystemTray
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

import javax.annotation.Resource
import javax.swing.BoxLayout
import javax.swing.JFrame
import javax.swing.JOptionPane as OP

@Log4j
class DefinitionsGUI {

    @Resource(name = "definitionsRestEndpoint")
	private final definitionsRestEndpoint
    
    @Resource(name = "shutdownListener")
	private final shutDownListener

    @Resource(name = "swingBuilder")
	private final swingBuilder
    
    @Resource(name = "stripeRenderer")
	private final stripeRender

	private resultsList

	private currentSelectedLocation
    
	private final deleteNoteDialogAction = {

		final toDelete = stripeRender.currentlySelected?.id ?: ""
		def optionPane = swingBuilder.optionPane()

		def nothingToDelete = {
			optionPane.showMessageDialog(null, "Please select a Note to delete", "Nothing to Delete", OP.PLAIN_MESSAGE)
		}

		def delete =  {
			def options = ["Yes", "No"]as Object[]
			def choice = optionPane.showOptionDialog(null, "Do you want to delete ${stripeRender.currentlySelected.name}", 'Delete Note',
			OP.YES_NO_OPTION, OP.QUESTION_MESSAGE, null, options, "")

			if(options[choice] == "Yes") {
				ds.delete(toDelete)
				searchForNoteAndDisplayResults("")
			}
		}

		toDelete == "" ? nothingToDelete() : delete()
	}

	void show() {
		final title = 'Notebook'
		swingBuilder.edt {
			def frame = frame(title: title, size:[100, 600], show: true, defaultCloseOperation: getDefaultCloseOperation(),
			pack:true, iconImage: getIconImage()) {
				lineBorder(color:Color.WHITE, thickness:10, parent:true)
				lookAndFeel("system")
				resultPanel()
				searchPanel()
				buttonPanel()
			}
			def sm = new DefinitionsGUIDisplayStateMachine(component: frame)
			addApplicationTrayIcon(title, frame, sm, shutDownListener)
			addSystemWideKeyBoardShortcuts(sm, shutDownListener)
		}
		searchForNoteAndDisplayResults("")
	}

	private def getIconImage() {
		new SwingBuilder().imageIcon(resource:'/document_text.png').getImage()
	}

	private final addOrEditNoteDialog (title, currentlySelected, addOrEditNote) {
		swingBuilder.dialog(id:'addOrEditDialog', modal: true, title: "${title} Note", size:[400, 220]) {
			panel(background: Color.WHITE, opaque: true) {
				boxLayout(axis:BoxLayout.Y_AXIS)
				lineBorder(color:Color.WHITE, thickness:10, parent:true)
				label("Name:")
				final name = textField(currentlySelected?.name ?: "")

				vstrut(height:10, opaque: true, background: Color.WHITE)
				label("Definition")
				final definitiion = textField(currentlySelected?.definition ?: "")

				vstrut(height:10, opaque: true, background: Color.WHITE)
				label("Description")
				final description = textField(currentlySelected?.description ?: "")

				vstrut(height:10, opaque: true, background: Color.WHITE)


				button(text: "${title}", actionPerformed: {
					addOrEditNote(currentlySelected?.id, name.text, definitiion.text, description.text)
					addOrEditDialog.dispose()
				})
			}
		}
	}

	private final resultPanel() {
		swingBuilder.scrollPane(constraints: BL.CENTER){
			resultsList = list(fixedCellWidth: 600, fixedCellHeight: 75, cellRenderer: stripeRender)
		}
	}

	private final buttonPanel() {
		swingBuilder.panel(constraints: BL.SOUTH, opaque: true, background: Color.WHITE) {
			flowLayout()

			button("Add", mnemonic: "A", actionPerformed: {
				addOrEditNoteDialog("Add", null, {id, name, definitiion, description ->
					ds.add(name, definitiion, description)
					searchForNoteAndDisplayResults(name)
				}).show()
			})

			button("Delete", mnemonic: "D", actionPerformed: deleteNoteDialogAction)

			button("Edit", mnemonic: "E", actionPerformed: {
				addOrEditNoteDialog("Edit", stripeRender.currentlySelected, {id,name, definitiion, description ->
					ds.edit(id, name, definitiion, description)
					searchForNoteAndDisplayResults(name)
				}).show()
			})

			button("Close", mnemonic: "C", actionPerformed: { shutDownListener.notifyOnClose() })
		}
	}

	private final searchPanel() {

		final keyListener = [ keyTyped: {
			final def handleEscape = {
				searchForNoteAndDisplayResults("")
				it.source.text = ""
			}
			it.keyChar == KeyEvent.VK_ESCAPE ? handleEscape(it) : searchForNoteAndDisplayResults(it.source.text)
		} ] as KeyAdapter

		final addButtonGroup = {
			def stores = ["test1", "test2"] 

			swingBuilder.panel(background: Color.WHITE, layout: new FlowLayout()) {
				myGroup = buttonGroup()
				stores.each {
					def r = radioButton(text:it, buttonGroup:myGroup, background: Color.WHITE,
					actionPerformed: {
						ds.updateCurrentStoreSource(it.source.text)
						
                        log.info("NEED TO IMPLEMENT ds.updateCurrentStoreSource(it.source.text)")
                        
                        searchForNoteAndDisplayResults("")
					})
				}
			}
		}

		swingBuilder.vbox(constraints: BL.NORTH) {
			addButtonGroup()
			final search = textField()
			search.addKeyListener(keyListener)
			vstrut(height:10, opaque: true, background: Color.WHITE)
		}
	}

	private def getDefaultCloseOperation() {
		SystemTray.isSupported() ? JFrame.DO_NOTHING_ON_CLOSE : JFrame.EXIT_ON_CLOSE
	}

	private void searchForNoteAndDisplayResults(d) {
		swingBuilder.doOutside {
			log.warn("NEED TO IMPLEMENT ds.getDefinitionsForQuery(d)")
            //final data = ds.getDefinitionsForQuery(d)
			//data.sort { it.name.toLowerCase() }
			//swingBuilder.edt { resultsList.listData = data }
		}
	}

	private void addSystemWideKeyBoardShortcuts(sm, shutdownListener) {
	
	}

	private void addApplicationTrayIcon(title, frame, sm, shutdownListener) {
		def ti = new DefinitionsTrayIcon(frame: frame, title: title, definitionsGUIDisplayStateMachine: sm, definitionsGUIShutDownListener: shutdownListener)
		ti.addTrayIcon()
		shutdownListener << ti
	}
}