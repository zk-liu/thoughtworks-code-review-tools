package net.lihui.app.plugin.thoughtworkscodereviewtools.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.CollectionComboBoxModel;
import lombok.extern.slf4j.Slf4j;
import net.lihui.app.plugin.thoughtworkscodereviewtools.intellij.store.TrelloBoardLabel;
import net.lihui.app.plugin.thoughtworkscodereviewtools.intellij.store.TrelloBoardLabelState;
import net.lihui.app.plugin.thoughtworkscodereviewtools.intellij.store.TrelloBoardMember;
import net.lihui.app.plugin.thoughtworkscodereviewtools.intellij.store.TrelloBoardMemberState;
import net.lihui.app.plugin.thoughtworkscodereviewtools.ui.dto.LabelDTO;
import net.lihui.app.plugin.thoughtworkscodereviewtools.ui.dto.OwnerDTO;
import net.lihui.app.plugin.thoughtworkscodereviewtools.ui.settingView.LabelComboboxRenderer;
import org.jdesktop.swingx.autocomplete.AutoCompleteComboBoxEditor;
import org.jdesktop.swingx.autocomplete.AutoCompleteDocument;
import org.jdesktop.swingx.autocomplete.ComboBoxAdaptor;
import org.jdesktop.swingx.autocomplete.ObjectToStringConverter;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Collections;
import java.util.List;

import static net.lihui.app.plugin.thoughtworkscodereviewtools.mapper.MemberMapper.MEMBER_MAPPER;

@Slf4j
public class CodeReviewPanel {
    private JPanel panel = new JPanel();
    private ComboBox<OwnerDTO> ownerComboBox;
    private JTextField feedBackText;
    private JButton refreshButton;
    private ComboBox<LabelDTO> labelComboBox;

    public CodeReviewPanel() {
        initOwnerComboBox();
        initLabelComboBox();
        feedBackText = new JTextField();
        feedBackText.setPreferredSize(new Dimension(200, 30));
        panel.add(feedBackText);
        refreshButton = new JButton("refresh");
        refreshButton.addActionListener(actionEvent -> {
            // refresh the member and the label data
        });
        panel.add(refreshButton);

    }

    private void initOwnerComboBox() {
        List<TrelloBoardMember> trelloBoardMembers = TrelloBoardMemberState.getInstance().getState().getTrelloBoardMembers();
        CollectionComboBoxModel comboBoxModel = new CollectionComboBoxModel(MEMBER_MAPPER.toDtoList(trelloBoardMembers));
        ownerComboBox = new ComboBox(comboBoxModel);
        OwnerDtoToStringConverter stringConverter = new OwnerDtoToStringConverter();
        setComboBox(stringConverter, ownerComboBox, new OwnerComboboxRenderer(), "owner");
    }

    private void initLabelComboBox() {
        List<TrelloBoardLabel> trelloBoardLabels = TrelloBoardLabelState.getInstance().getState().getTrelloBoardLabels();
        CollectionComboBoxModel comboBoxModel = new CollectionComboBoxModel(MEMBER_MAPPER.toLabelDtoList(trelloBoardLabels));
        LabelDtoToStringConverter stringConverter = new LabelDtoToStringConverter();
        labelComboBox = new ComboBox(comboBoxModel);
        setComboBox(stringConverter, labelComboBox, new LabelComboboxRenderer(), "Label");
    }

    private void setComboBox(ObjectToStringConverter converter, ComboBox comboBox, ListCellRenderer renderer, String tipText) {
        comboBox.setRenderer(renderer);
        comboBox.setEditable(true);
        AutoCompleteComboBoxEditor editor = new AutoCompleteComboBoxEditor(comboBox.getEditor(), converter);
        JTextComponent editorComponent = (JTextComponent) comboBox.getEditor().getEditorComponent();
        AutoCompleteDocument autoCompleteDocument = new AutoCompleteDocument(new ComboBoxAdaptor(comboBox), false, converter);
        editorComponent.setDocument(autoCompleteDocument);
        comboBox.setEditor(editor);
        comboBox.setMaximumRowCount(5);
        comboBox.setSelectedItem(null);
        comboBox.setToolTipText(tipText);
        panel.add(comboBox);
    }

    public JPanel getPanel() {
        return panel;
    }

    public FeedBackContext getFeedbackContext() {
        return FeedBackContext.builder()
                .feedback(feedBackText.getText())
                .memberList(Collections.singletonList(MEMBER_MAPPER.toMember(ownerComboBox.getItem())))
                .label(MEMBER_MAPPER.toLabel(labelComboBox.getItem()))
                .build();
    }
}
