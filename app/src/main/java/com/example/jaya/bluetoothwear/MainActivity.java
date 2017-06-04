package com.example.jaya.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.animation.ChartAnimationListener;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.Chart;
import lecho.lib.hellocharts.view.LineChartView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "IPMC>>>>>>>>>>";
    private static final boolean D = true;
    private static Intent intent = null;

    private Handler mUiHandler = new Handler();
    private MyWorkerThread mWorkThread;

    Button btnOpenBt, btnCloseBt, btnConnBt;

    private BluetoothAdapter btMyAdapter = BluetoothAdapter.getDefaultAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        findViewById();

        if (D) {
            Log.e(TAG, "+++ ON CREATE +++");
        }

        mWorkThread = new MyWorkerThread("myWorkerThread");
        mWorkThread.start();
        mWorkThread.prepareHandler();

        Runnable initialBtTask = new Runnable() {
            @Override
            public void run() {

                if (!btMyAdapter.isEnabled()) {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnConnBt.setEnabled(false);
                            btnCloseBt.setEnabled(false);
                        }
                    });
                } else {
                    mUiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            btnOpenBt.setEnabled(false);
                            btnConnBt.setEnabled(true);
                        }
                    });
                }
            }
        };
        mWorkThread.postTask(initialBtTask);
    }

    @Override
    public void onStart() {

        super.onStart();

        if (D) {
            Log.e(TAG, "++++++ ON START ++++++");
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (D) {
            Log.e(TAG, "++++++ ON RESUME ++++++");
        }
    }

    @Override
    public void onPause() {

        super.onPause();

        if (D) {
            Log.e(TAG, "······· ON PAUSE ·······");
        }

    }

    @Override
    public void onStop() {

        super.onStop();

        if (D) {
            Log.e(TAG, "/////// ON STOP ///////");
        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (D) {
            Log.e(TAG, "====== ON DESTROY ======");
        }

        mWorkThread.quit();
        stopService(intent);
    }

    private void findViewById () {
        btnOpenBt = (Button) findViewById(R.id.btnOpenBt);
        btnCloseBt = (Button) findViewById(R.id.btnCloseBt);
        btnConnBt = (Button) findViewById(R.id.btnConnBt);
    }

    /* 开启蓝牙 */
    public void onEnableButtonClicked (View view) {
        Runnable openBtTask = new Runnable() {
            @Override
            public void run() {
                btMyAdapter.enable();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnOpenBt.setEnabled(false);
                        btnConnBt.setEnabled(true);
                        btnCloseBt.setEnabled(true);
                    }
                });
            }
        };
        mWorkThread.postTask(openBtTask);
    }


    /* 关闭蓝牙 */
    public void onDisableButtonClicked (View view) {
        Runnable closeBtTask = new Runnable() {
            @Override
            public void run() {
                btMyAdapter.disable();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        btnOpenBt.setEnabled(true);
                        btnConnBt.setEnabled(false);
                        btnCloseBt.setEnabled(false);
                    }
                });
            }
        };
        mWorkThread.postTask(closeBtTask);
    }


    /* 配对 */
    public void onStartDiscoveryButtonClicked (View view)
    {
        intent = new Intent(MainActivity.this, ReadBluetoothService.class );
        startService(intent);
        btnConnBt.setEnabled(false);
    }

/*    public void DisplayToast (String str) {
        Toast toast=Toast.makeText(this, str, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 220);
        toast.show();
    }*/

    public class MyWorkerThread extends HandlerThread {
        private Handler mWorkerHandler;

        public MyWorkerThread(String name) {
            super(name);
        }

        public void prepareHandler() {
            mWorkerHandler = new Handler(getLooper());
        }

        public void postTask(Runnable task) {
            mWorkerHandler.post(task);
        }
    }

    /**
     * A fragment containing a line chart.
     */
    public static class PlaceholderFragment extends Fragment {

        private BtDataReceiver btDataReceiver;
        private float FloatBtReceiveData;
        public LineChartView chart;
        private LineChartData data;
        private int numberOfLines = 1;
        private int maxNumberOfLines = 4;
        private int numberOfPoints = 12;
        private float theLastXValue = (float)numberOfPoints;

        float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];

        private boolean hasAxes = true;
        private boolean hasAxesNames = true;
        private boolean hasLines = true;
        private boolean hasPoints = true;
        private ValueShape shape = ValueShape.CIRCLE;
        private boolean isFilled = false;
        private boolean hasLabels = false;
        private boolean isCubic = false;
        private boolean hasLabelForSelected = false;
        private boolean pointsHaveDifferentColor;
//        private boolean hasGradientToTransparent = false;

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach (Context context) {
            // TODO: 2017/6/4 fragment receiver
            super.onAttach(context);
            btDataReceiver = new BtDataReceiver();
            IntentFilter filter = new IntentFilter(ReadBluetoothService.ACTION);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(btDataReceiver, filter);

            Log.e("TAG", "onAttach is Done");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_line_chart, container, false);

            chart = (LineChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            // Generate some random values.
            generateValues();

            generateData();

            // Disable viewport recalculations, see toggleCubic() method for more info.
            chart.setViewportCalculationEnabled(false);

            resetViewport();

            return rootView;
        }

        // MENU
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.main, menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_reset) {
                reset();
                generateData();
                return true;
            }
/*            if (id == R.id.action_add_line) {
                addLineToData();
                return true;
            }*/
            if (id == R.id.action_toggle_lines) {
                toggleLines();
                return true;
            }
            if (id == R.id.action_toggle_points) {
                togglePoints();
                return true;
            }
            if (id == R.id.action_toggle_cubic) {
                toggleCubic();
                return true;
            }
            if (id == R.id.action_toggle_area) {
                toggleFilled();
                return true;
            }
            if (id == R.id.action_toggle_labels) {
                toggleLabels();
                return true;
            }
            if (id == R.id.action_toggle_axes) {
                toggleAxes();
                return true;
            }
            if (id == R.id.action_toggle_axes_names) {
                toggleAxesNames();
                return true;
            }
/*            if (id == R.id.action_animate) {
                prepareDataAnimation();
                chart.startDataAnimation();
                return true;
            }*/
            if (id == R.id.action_toggle_selection_mode) {
                toggleLabelForSelected();

                Toast.makeText(getActivity(),
                        "Selection mode set to " + chart.isValueSelectionEnabled() + " select any point.",
                        Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_toggle_touch_zoom) {
                chart.setZoomEnabled(!chart.isZoomEnabled());
                Toast.makeText(getActivity(), "IsZoomEnabled " + chart.isZoomEnabled(), Toast.LENGTH_SHORT).show();
                return true;
            }
            if (id == R.id.action_zoom_both) {
                chart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
                return true;
            }
            if (id == R.id.action_zoom_horizontal) {
                chart.setZoomType(ZoomType.HORIZONTAL);
                return true;
            }
            if (id == R.id.action_zoom_vertical) {
                chart.setZoomType(ZoomType.VERTICAL);
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void generateValues() {
            for (int i = 0; i < maxNumberOfLines; ++i) {
                for (int j = 0; j < numberOfPoints; ++j) {
                    randomNumbersTab[i][j] = 0f;
                }
            }
        }

        private void reset() {
            numberOfLines = 1;

            hasAxes = true;
            hasAxesNames = true;
            hasLines = true;
            hasPoints = true;
            shape = ValueShape.CIRCLE;
            isFilled = false;
            hasLabels = false;
            isCubic = false;
            hasLabelForSelected = false;
            pointsHaveDifferentColor = false;

            chart.setValueSelectionEnabled(hasLabelForSelected);
            resetViewport();
        }

        private void resetViewport() {
            // Reset viewport height range to (0,100)
            final Viewport v = new Viewport(chart.getMaximumViewport());
            v.bottom = 0;
            v.top = 20;
            v.left = 0;
            v.right = numberOfPoints - 1;
            chart.setMaximumViewport(v);
            chart.setCurrentViewport(v);
        }

        private void generateData() {

            List<Line> lines = new ArrayList<Line>();
            for (int i = 0; i < numberOfLines; ++i) {

                List<PointValue> values = new ArrayList<PointValue>();
                for (int j = 0; j < numberOfPoints; ++j) {
                    values.add(new PointValue(j, randomNumbersTab[i][j]));
                }

                Line line = new Line(values);
                line.setColor(ChartUtils.COLORS[i]);
                line.setShape(shape);
                line.setCubic(isCubic);
                line.setFilled(isFilled);
                line.setHasLabels(hasLabels);
                line.setHasLabelsOnlyForSelected(hasLabelForSelected);
                line.setHasLines(hasLines);
                line.setHasPoints(hasPoints);
//                line.setHasGradientToTransparent(hasGradientToTransparent);
                if (pointsHaveDifferentColor){
                    line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
                }
                lines.add(line);
            }

            data = new LineChartData(lines);

            if (hasAxes) {
                Axis axisX = new Axis();
                Axis axisY = new Axis().setHasLines(true);
                if (hasAxesNames) {
                    axisX.setName("Time /s");
                    axisY.setName("Voltage /mV");
                }
                data.setAxisXBottom(axisX);
                data.setAxisYLeft(axisY);
            } else {
                data.setAxisXBottom(null);
                data.setAxisYLeft(null);
            }

            data.setBaseValue(Float.NEGATIVE_INFINITY);
            chart.setLineChartData(data);

        }

        /**
         * Adds lines to data, after that data should be set again with
         * {@link LineChartView#setLineChartData(LineChartData)}. Last 4th line has non-monotonically x values.
         */
  /*      private void addLineToData() {
            if (data.getLines().size() >= maxNumberOfLines) {
                Toast.makeText(getActivity(), "Samples app uses max 4 lines!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                ++numberOfLines;
            }

            generateData();
        }*/

        private void toggleLines() {
            hasLines = !hasLines;

            generateData();
        }

        private void togglePoints() {
            hasPoints = !hasPoints;

            generateData();
        }

        private void toggleCubic() {
            isCubic = !isCubic;

            generateData();

            if (isCubic) {
                // It is good idea to manually set a little higher max viewport for cubic lines because sometimes line
                // go above or below max/min. To do that use Viewport.inest() method and pass negative value as dy
                // parameter or just set top and bottom values manually.
                // In this example I know that Y values are within (0,100) range so I set viewport height range manually
                // to (-5, 105).
                // To make this works during animations you should use Chart.setViewportCalculationEnabled(false) before
                // modifying viewport.
                // Remember to set viewport after you call setLineChartData().
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = -5;
                v.top = 105;
                // You have to set max and current viewports separately.
                chart.setMaximumViewport(v);
                // I changing current viewport with animation in this case.
                chart.setCurrentViewportWithAnimation(v);
            } else {
                // If not cubic restore viewport to (0,100) range.
                final Viewport v = new Viewport(chart.getMaximumViewport());
                v.bottom = 0;
                v.top = 100;

                // You have to set max and current viewports separately.
                // In this case, if I want animation I have to set current viewport first and use animation listener.
                // Max viewport will be set in onAnimationFinished method.
                chart.setViewportAnimationListener(new ChartAnimationListener() {

                    @Override
                    public void onAnimationStarted() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onAnimationFinished() {
                        // Set max viewpirt and remove listener.
                        chart.setMaximumViewport(v);
                        chart.setViewportAnimationListener(null);

                    }
                });
                // Set current viewpirt with animation;
                chart.setCurrentViewportWithAnimation(v);
            }

        }

        private void toggleFilled() {
            isFilled = !isFilled;

            generateData();
        }

        private void toggleLabels() {
            hasLabels = !hasLabels;

            if (hasLabels) {
                hasLabelForSelected = false;
                chart.setValueSelectionEnabled(hasLabelForSelected);
            }

            generateData();
        }

        private void toggleLabelForSelected() {
            hasLabelForSelected = !hasLabelForSelected;

            chart.setValueSelectionEnabled(hasLabelForSelected);

            if (hasLabelForSelected) {
                hasLabels = false;
            }

            generateData();
        }

        private void toggleAxes() {
            hasAxes = !hasAxes;

            generateData();
        }

        private void toggleAxesNames() {
            hasAxesNames = !hasAxesNames;

            generateData();
        }

        /**
         * To animate values you have to change targets values and then call {@link Chart#startDataAnimation()}
         * method(don't confuse with View.animate()). If you operate on data that was set before you don't have to call
         * {@link LineChartView#setLineChartData(LineChartData)} again.
         */

        private void prepareDataAnimation(float btReceiveData) {
            for (Line line : data.getLines()) {
                for (PointValue value : line.getValues()) {
                    value.setTarget(value.getX() - 1, value.getY());
                    value.setTarget(theLastXValue, btReceiveData);
                }
            }
        }

        // TODO: 2017/5/27 service与activity通讯
        public class BtDataReceiver extends BroadcastReceiver {
            @Override
            public void onReceive (Context context, Intent intent) {
                String btReceiveData = intent.getStringExtra("btReceiveData");
                btReceiveData = btReceiveData.substring(11, 15);
                FloatBtReceiveData = Float.parseFloat(btReceiveData) / 1000f;

                Log.e("TAG", btReceiveData);
                prepareDataAnimation(FloatBtReceiveData);
                chart.startDataAnimation();
            }
        }

        private class ValueTouchListener implements LineChartOnValueSelectListener {

            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {
                Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(btDataReceiver);
        }
    }

}
