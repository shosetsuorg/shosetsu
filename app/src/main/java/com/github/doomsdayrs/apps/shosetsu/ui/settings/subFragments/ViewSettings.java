package com.github.doomsdayrs.apps.shosetsu.ui.settings.subFragments;
/*
 * This file is part of Shosetsu.
 *
 * Shosetsu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Shosetsu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Shosetsu.  If not, see <https://www.gnu.org/licenses/>.
 * ====================================================================
 * Shosetsu
 * 13 / 07 / 2019
 *
 * @author github.com/doomsdayrs
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.github.doomsdayrs.apps.shosetsu.R;
import com.github.doomsdayrs.apps.shosetsu.variables.Settings;
import com.github.doomsdayrs.apps.shosetsu.variables.recycleObjects.SettingsItem;

import java.util.ArrayList;
import java.util.List;

import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeIndentSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.changeParagraphSpacing;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isReaderNightMode;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.isTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setNightNode;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.setTextSize;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.toggleTapToScroll;
import static com.github.doomsdayrs.apps.shosetsu.backend.Utilities.unsetNightMode;

// TODO: Migrate to using PreferenceScreen and PreferenceGroup.
public class ViewSettings extends Fragment {
    private static final List<String> textSizes = new ArrayList<>();
    private static final List<String> paragraphSpaces = new ArrayList<>();
    private static final List<String> indentSizes = new ArrayList<>();

    static {
        textSizes.add("Small");
        textSizes.add("Medium");
        textSizes.add("Large");

        paragraphSpaces.add("None");
        paragraphSpaces.add("Small");
        paragraphSpaces.add("Medium");
        paragraphSpaces.add("Large");

        indentSizes.add("None");
        indentSizes.add("Small");
        indentSizes.add("Medium");
        indentSizes.add("Large");
    }

    private Spinner paragraphSpacing;
    private Spinner textSize;
    private Spinner indentSize;
    private Switch tap_to_scroll;

    //TODO remove this abomination of code. We just need to make a simple old switch
    private void onClickNIghtMode(View v) {
        if (this.getContext() != null) {
            SettingsItem nightMOdeItem = new SettingsItem(v);
            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
            builder.setTitle(R.string.reader_night_mode);
            String[] states = {getString(R.string.on), getString(R.string.off)};
            builder.setItems(states,
                    (dialogInterface, i) -> {
                        if (i == 0) setNightNode();
                        else unsetNightMode();

                        int nightModeStatus = isReaderNightMode() ?
                                R.string.on : R.string.off;
                        nightMOdeItem.setDesc(nightModeStatus);
                        nightMOdeItem.invalidate();
                    }
            );
            builder.show();
        }
    }

    /*
    private void onClickTextSize(View v) {

    }

    private void onClickParaSpacing(View v) {

    }

    private void onClickParaIndent(View v) {

    }
*/
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("OnCreateView", "ViewSettings");
        View settingsReaderView = inflater.inflate(R.layout.settings_view, container, false);

        // Setup Night Mode
        SettingsItem nightModeItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_night_mode));
        nightModeItem.setTitle(R.string.reader_night_mode);
        int nightModeStatus = isReaderNightMode() ?
                R.string.on : R.string.off;
        nightModeItem.setDesc(nightModeStatus);
        nightModeItem.setOnClickListener(this::onClickNIghtMode);

        // Setup Text size
        // SettingsItem textSizeItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_text_size));
        // textSizeItem.setTitle(R.string.text_size);
        // TODO: Get current Text size

        // Setup Paragraph Spacing
        // SettingsItem paraSpacingItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_spacing));
        // paraSpacingItem.setTitle(R.string.spacing);
        // TODO: Get current Paragraph spacing

        // Setup Indent Size
        // SettingsItem paraIndentItem = new SettingsItem(settingsReaderView.findViewById(R.id.settings_reader_para_indent));
        // paraIndentItem.setTitle(R.string.indent_size);
        // TODO: Get current Indent size

        paragraphSpacing = settingsReaderView.findViewById(R.id.reader_paragraphSpacing);
        textSize = settingsReaderView.findViewById(R.id.reader_textSize);
        indentSize = settingsReaderView.findViewById(R.id.reader_indentSize);
        tap_to_scroll = settingsReaderView.findViewById(R.id.tap_to_scroll);

        //TODO figure out why the itemSelectedListner runs without being selected
        if (getContext() != null) {
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, textSizes);
                textSize.setAdapter(dataAdapter);
                switch ((int) Settings.ReaderTextSize) {
                    default:
                    case 14:
                        textSize.setSelection(0);
                        break;
                    case 17:
                        textSize.setSelection(1);
                        break;
                    case 20:
                        textSize.setSelection(2);
                        break;
                }

                textSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 2) {
                            int size = 14;
                            switch (i) {
                                case 0:
                                    break;
                                case 1:
                                    size = 17;
                                    break;
                                case 2:
                                    size = 20;
                                    break;
                            }
                            setTextSize(size);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });
            }
            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, paragraphSpaces);
                int spaceBack = Settings.paragraphSpacing;

                switch (Settings.paragraphSpacing) {
                    case 0:
                        paragraphSpacing.setSelection(0);
                        break;
                    case 1:
                        paragraphSpacing.setSelection(1);
                        break;
                    case 2:
                        paragraphSpacing.setSelection(2);
                        break;
                    case 3:
                        paragraphSpacing.setSelection(3);
                        break;
                }

                paragraphSpacing.setAdapter(dataAdapter);

                paragraphSpacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 3) {
                            changeParagraphSpacing(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                changeParagraphSpacing(spaceBack);
                switch (Settings.paragraphSpacing) {
                    case 0:
                        paragraphSpacing.setSelection(0);
                        break;
                    case 1:
                        paragraphSpacing.setSelection(1);
                        break;
                    case 2:
                        paragraphSpacing.setSelection(2);
                        break;
                    case 3:
                        paragraphSpacing.setSelection(3);
                        break;
                }

            }

            {
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, indentSizes);
                int spaceBack = Settings.indentSize;
                indentSize.setSelection(Settings.indentSize);
                indentSize.setAdapter(dataAdapter);
                indentSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, android.view.View view, int i, long l) {
                        if (i >= 0 && i <= 3) {
                            changeIndentSize(i);
                            adapterView.setSelection(i);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                changeIndentSize(spaceBack);
                indentSize.setSelection(Settings.indentSize);
            }

            {
                tap_to_scroll.setChecked(isTapToScroll());
                tap_to_scroll.setOnCheckedChangeListener((compoundButton, b) -> toggleTapToScroll());
            }


        }

        return settingsReaderView;
    }
}
