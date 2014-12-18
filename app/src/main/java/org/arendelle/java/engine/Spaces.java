
//
//  JArendelle - Java Portation of the Arendelle Language
//  Copyright (c) 2014 Micha Hanselmann <h@arendelle.org>
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
//

package org.arendelle.java.engine;

import java.util.SortedMap;
import java.util.logging.LogRecord;

import org.arendelle.android.Main;
import org.arendelle.android.R;
import org.arendelle.android.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class Spaces {

    /** Activity instance for input dialogs */
    static Activity context;


	/** replaces all spaces (variables) in the given expression with their values
	 * @param expression
	 * @return The final expression
	 */
	public static String replace(String expression, CodeScreen screen, SortedMap<String, String> spaces) {
		
		for (String name : spaces.keySet()) {
			expression = expression.replaceAll('@' + name, spaces.get(name));
		}
		
		return expression;
	}
	
	/** Spaces kernel which parses and edit spaces
	 * @param arendelle a given Arendelle instance
	 * @param screen
	 * @param spaces
	 */
	public static void parse(final Arendelle arendelle, final CodeScreen screen, final SortedMap<String, String> spaces) {
		
		// determine if it should be a stored space
		if (arendelle.code.charAt(arendelle.i + 1) == '$') {
			StoredSpaces.parse(arendelle, screen, spaces);
			return;
		}
		
		// get name
		String name = "";
		for (int i = arendelle.i + 1; !(arendelle.code.charAt(i) == ',' || arendelle.code.charAt(i) == ')'); i++) {
			name += arendelle.code.charAt(i);
			arendelle.i = i;
		}
		
		// get mathematical expression for condition
		String expression = "";
		int nestedGrammars = 0;
		if (arendelle.code.charAt(arendelle.i + 1) == ',') {
			for (int i = arendelle.i + 2; !(arendelle.code.charAt(i) == ')' && nestedGrammars == 0); i++) {
				expression += arendelle.code.charAt(i);
				arendelle.i = i;
				
				if (arendelle.code.charAt(i) == '[' || arendelle.code.charAt(i) == '{' || arendelle.code.charAt(i) == '(') {
					nestedGrammars++;
				} else if (arendelle.code.charAt(i) == ']' || arendelle.code.charAt(i) == '}' || arendelle.code.charAt(i) == ')') {
					nestedGrammars--;
				}
				
			}
		}
		
		arendelle.i++;
		
		// determine action
		if (expression == "") {
			
			// get user input
			if (!screen.interactiveMode) {
                Reporter.report("Not running in Interactive Mode!", arendelle.line);
                return;
            }

            /*/ create input dialog
            if (context == null) return;
            final String finalName = name;
            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = context.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.dialog_input, null);
            ((TextView) dialogView.findViewById(R.id.dialog_input_number)).setHint(String.format(context.getText(R.string.dialog_input_sign_space).toString(), name));
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    spaces.put(finalName, ((EditText) dialogView.findViewById(R.id.dialog_input_number)).getText().toString());

                }
            });
            builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = builder.create();
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    dialog.show();
                }
            });
            arendelle.i = arendelle.code.length();*/

		} else if (expression.equals("done")) {
			
			// remove space
			spaces.remove(name);
			return;
			
		} else {
			
			switch(expression.charAt(0)) {
			
			case '"':
				
				// get user input by message
				if (!screen.interactiveMode) {
					Reporter.report("Not running in Interactive Mode!", arendelle.line);
					return;
				}
				//TODO:String value = JOptionPane.showInputDialog(expression.substring(1, expression.length() - 1));
				//TODO:spaces.put(name, String.valueOf(new Expression(Replacer.replace(value, screen, spaces)).eval().intValue()));
				
				break;
				
			case '+':
			case '-':
			case '*':
			case '/':
				// edit space
				spaces.put(name, String.valueOf(new Expression(Replacer.replace(spaces.get(name) + expression.charAt(0) + expression.substring(1), screen, spaces)).eval().intValue()));
				break;
				
			default:
				// create space
				spaces.put(name, String.valueOf(new Expression(Replacer.replace(expression, screen, spaces)).eval().intValue()));
				break;
				
			}
			
		}
		
	}

    /** initalizes spaces kernel */
	public static void init(Activity context) {
        Spaces.context = context;
    }
	
}
