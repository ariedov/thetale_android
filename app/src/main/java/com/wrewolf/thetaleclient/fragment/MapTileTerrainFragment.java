package com.wrewolf.thetaleclient.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wrewolf.thetaleclient.R;
import com.wrewolf.thetaleclient.api.response.MapCellResponse;
import com.wrewolf.thetaleclient.util.UiUtils;

/**
 * @author Hamster
 * @since 15.10.2014
 */
public class MapTileTerrainFragment extends TabbedDialogTabFragment {

    private static final String PARAM_CELL_INFO = "PARAM_CELL_INFO";

    public static MapTileTerrainFragment newInstance(final MapCellResponse cellInfo) {
        final MapTileTerrainFragment dialog = new MapTileTerrainFragment();

        final Bundle args = new Bundle();
        args.putParcelable(PARAM_CELL_INFO, cellInfo);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    protected void setupContent(final LayoutInflater layoutInflater, final ViewGroup container) {
        final View content = layoutInflater.inflate(R.layout.tabbed_dialog_tab_content_text, container, true);
        final TextView text = (TextView) content.findViewById(R.id.tabbed_dialog_tab_content_text);

        final MapCellResponse cellInfo = getArguments().getParcelable(PARAM_CELL_INFO);
        final StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for(final String line : cellInfo.terrain) {
            if(first) {
                first = false;
            } else {
                stringBuilder.append("\n");
            }
            stringBuilder.append(line);
        }
        UiUtils.setText(text, stringBuilder.toString());
    }

}
