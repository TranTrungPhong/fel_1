package com.framgia.fel1.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.framgia.fel1.R;
import com.framgia.fel1.constant.Const;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by vuduychuong1994 on 5/3/16.
 */
public class ExportFile {
    private static final String CSV_TYPE = ".csv";
    private static final String TSV_TYPE = ".tsv";
    private static final String PDF_TYPE = ".pdf";

    public static boolean exportCsv(Context context, String name, String data) throws IOException {
        File file = null;
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root.getAbsolutePath() + "/" +
                    context.getResources().getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, name + CSV_TYPE);
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            out.write(data.getBytes());
            out.close();
            return true;
        } else {
            return false;
        }
    }

    public static boolean exportTsv(Context context, String name, String data) throws IOException {
        File file = null;
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root.getAbsolutePath() + "/" +
                    context.getResources().getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, name + TSV_TYPE);
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            out.write(data.getBytes());
            out.close();
            return true;
        } else {
            return false;
        }
    }

    public static boolean exportPdf(Context context, String name, ArrayList<String> arrayList,
                                    int numColumns) throws IOException, DocumentException {

        File file = null;
        File root = Environment.getExternalStorageDirectory();
        if (root.canWrite()) {
            File dir = new File(root.getAbsolutePath() + "/" +
                    context.getResources().getString(R.string.app_name));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir, name + PDF_TYPE);
            FileOutputStream out = null;
            out = new FileOutputStream(file);
            Document document = new Document();
            document.addTitle(name);
            PdfWriter.getInstance(document, out);
            document.open();
            PdfPTable table = new PdfPTable(numColumns);
            table.setHorizontalAlignment(Element.ALIGN_CENTER);
            for (String s : arrayList) {
                table.addCell(new PdfPCell(new Phrase(s)));
            }
            document.add(table);
            document.close();
            out.close();
            return true;
        } else {
            return false;
        }


    }
}
