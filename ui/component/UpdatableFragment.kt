package com.ionob.pos.ui.component

import android.support.v4.app.Fragment
import com.ionob.pos.domain.inventory.Product
import com.ionob.pos.ui.inventory.CilckCallBack

/**
 * Fragment which is able to call update() from other class.
 * This is used by Delegation pattern.
 *
 * @author Ionob Team
 */
abstract class UpdatableFragment : Fragment() , CilckCallBack {

    /**
     * Update fragment.
     */
    abstract fun update()

    override fun onClick(data: Product?) {

    }
}
