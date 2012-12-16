package edu.spbstu.wfsmp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.spbstu.wfsmp.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * User: artegz
 * Date: 14.10.12
 * Time: 15:05
 */
public class ViewResultsActivity extends Activity {

    // todo implement me

    final List<String> results = new ArrayList<String>();

    {
        // todo use table
        if (ApplicationContext.getInstance().mockDevice) {
            final StringBuilder sb = new StringBuilder();

            sb.append("����� ���������");
            sb.append("\t");
            sb.append("���������");
            sb.append("\t");
            sb.append("�������");
            sb.append("\t");
            sb.append("�������� ������");
            sb.append("\t");
            sb.append("������� ��������");
            sb.append("\t");
            sb.append("����� ��������");
            sb.append("\t");
            sb.append("�������� ���������");
            sb.append("\t");
            sb.append("��� ��������");
            sb.append("\t");
            sb.append("���� ���������");
            sb.append("\t");

            results.add(sb.toString());
            results.add("0\t10\t20\t5\t10\t50\t100\t1\t16.06.2012");
            results.add("1\t10\t20\t5\t10\t50\t100\t1\t16.06.2012");
            results.add("3\t10\t20\t5\t10\t50\t100\t1\t16.06.2012");

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);


        final ListView view = (ListView) findViewById(R.id.resultsList);

        // set adapter which provide device list
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(view.getContext(),
                R.layout.select_device,
                R.id.text_row,
                results);

        view.setAdapter(arrayAdapter);


    }
}
