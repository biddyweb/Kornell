package kornell.gui.client.util.view.formfield;

import kornell.gui.client.presentation.util.FormHelper;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class TextBoxFormField implements KornellFormField<TextBox> {

	private final FormHelper formHelper = GWT.create(FormHelper.class);
	
	public static final String CPF = "cpf";
	
	TextBox field;
	String textBoxFormFieldType;
	
	public TextBoxFormField(TextBox field, String textBoxFormFieldType) {
		this.field = field;
		this.textBoxFormFieldType = textBoxFormFieldType;
		field.addStyleName("field");
		field.addStyleName("textField");
		
	}
	
	public TextBoxFormField(TextBox field) {
		this(field, null);
	}

	@Override
	public Widget getFieldWidget() {
		return field;
	}

	@Override
	public String getDisplayText() {
		if(CPF.equals(textBoxFormFieldType))
			return formHelper.formatCPF(field.getValue());
		else
			return field.getValue();
	}

	@Override
	public String getPersistText() {
		return field.getValue();
	}

}
