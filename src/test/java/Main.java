// (c) 2017 uchicom


import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try (PDDocument doc = PDDocument.load(new File("template/23100051-02.pdf"))) {
			PDAcroForm form = doc.getDocumentCatalog().getAcroForm();
			int count = 0;
			for (PDField field : form.getFields()) {
				switch (field.getFieldType()) {
				case "Tx":
					field.setValue("1");
					break;
				case "Ch":
					field.setValue("0");
					break;
				}
				count++;
				if (count > 5) break;
				System.out.println(field.getFieldType());
				System.out.println(field.getFieldFlags());
				System.out.println(field.getFullyQualifiedName());
				System.out.println(field.isNoExport());
				System.out.println(field.isReadOnly());
				System.out.println(field.isRequired());
				System.out.println(field.getPartialName());
				System.out.println(field.getValueAsString());
				System.out.println(field.toString());
			}
			doc.save(new File("result/test.pdf"));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
