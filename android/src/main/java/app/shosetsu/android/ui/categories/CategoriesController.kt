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
 *
 */

package app.shosetsu.android.ui.categories

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.shosetsu.android.R
import app.shosetsu.android.common.ext.logE
import app.shosetsu.android.common.ext.makeSnackBar
import app.shosetsu.android.common.ext.viewModel
import app.shosetsu.android.databinding.CategoriesAddBinding
import app.shosetsu.android.view.compose.ShosetsuCompose
import app.shosetsu.android.view.controller.ShosetsuController
import app.shosetsu.android.view.controller.base.ExtendedFABController
import app.shosetsu.android.view.controller.base.syncFABWithCompose
import app.shosetsu.android.view.uimodels.model.CategoryUI
import app.shosetsu.android.viewmodel.abstracted.ACategoriesViewModel
import kotlinx.coroutines.flow.Flow

class CategoriesController : ShosetsuController(), ExtendedFABController {

    val viewModel: ACategoriesViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedViewState: Bundle?
    ): View {
        setViewTitle()
        return ComposeView(requireContext()).apply {
            setContent {
                ShosetsuCompose {
                    val items by viewModel.liveData.collectAsState(emptyList())


                    CategoriesContent(
                        items = items,
                        onRemove = {
                            onRemove(it, context)
                        },
                        onMoveUp = {
                            viewModel.moveUp(it).observeMoveCategory()
                        },
                        onMoveDown = {
                            viewModel.moveDown(it).observeMoveCategory()
                        },
                        fab = fab
                    )
                }
            }
        }
    }

    private fun onRemove(item: CategoryUI, context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.alert_dialog_title_warn_categories_removal)
            .setMessage(R.string.alert_dialog_message_warn_categories_removal)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                removeCategory(item)
            }.setNegativeButton(android.R.string.cancel) { _, _ ->
            }.show()
    }

    private fun removeCategory(item: CategoryUI) {
        // Pass item to viewModel to remove, observe result
        viewModel.remove(item).observe(
            catch = {
                logE("Failed to remove category $item", it)
                makeSnackBar(R.string.toast_categories_remove_fail)
                    ?.setAction(R.string.generic_question_retry) {
                        removeCategory(item)
                    }?.show()
            }
        ) {
            // Inform user of the category being removed
            makeSnackBar(
                R.string.controller_categories_snackbar_repo_removed,
            )?.show()
        }
    }


    private fun addCategory(name: String) {
        viewModel.addCategory(name).observe(
            catch = {
                // Inform the user the category couldn't be added
                makeSnackBar(R.string.toast_categories_add_fail)?.show()
            }
        ) {
            // Inform the user that the category was added
            makeSnackBar(R.string.toast_categories_added)?.show()
        }
    }

    private fun Flow<Unit>.observeMoveCategory() {
        observe(
            catch = {
                // Inform the user the category couldn't be added
                makeSnackBar(R.string.toast_categories_move_fail)?.show()
            }
        ) {}
    }

    private fun launchAddCategoryDialog(view: View) {
        val addBinding = CategoriesAddBinding.inflate(LayoutInflater.from(view.context))

        AlertDialog.Builder(view.context)
            .setView(addBinding.root)
            .setTitle(R.string.categories_add_title)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                with(addBinding) {
                    // Pass data to view model, observe result
                    addCategory(
                        nameInput.text.toString(),
                    )
                }
            }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
    }

    private lateinit var fab: ExtendedFABController.EFabMaintainer
    override fun manipulateFAB(fab: ExtendedFABController.EFabMaintainer) {
        this.fab = fab
        fab.setIconResource(R.drawable.add_circle_outline)
        fab.setText(R.string.controller_categories_action_add)

        // When the FAB is clicked, open a alert dialog to input a new category
        fab.setOnClickListener { launchAddCategoryDialog(it) }
    }

}

@Composable
fun CategoriesContent(
    items: List<CategoryUI>,
    onRemove: (CategoryUI) -> Unit,
    onMoveUp: (CategoryUI) -> Unit,
    onMoveDown: (CategoryUI) -> Unit,
    fab: ExtendedFABController.EFabMaintainer
) {
    val state = rememberLazyListState()
    syncFABWithCompose(state, fab)

    LazyColumn(
        Modifier.fillMaxSize(),
        state,
        contentPadding = PaddingValues(bottom = 64.dp, top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) {
            Card {
                Column(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(it.name, style = MaterialTheme.typography.h6)
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { onMoveDown(it) }) {
                            Icon(painterResource(R.drawable.expand_less), contentDescription = null)
                        }
                        IconButton(onClick = { onMoveUp(it) }) {
                            Icon(painterResource(R.drawable.expand_more), contentDescription = null)
                        }
                        IconButton(onClick = { onRemove(it) }) {
                            Icon(painterResource(R.drawable.trash), contentDescription = null)
                        }
                    }
                }
            }
        }
    }
}