package suman.com.andoirdbluetoothprint


import java.io.IOException
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.UUID

import android.app.Activity
import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.ionob.pos.R
import kotlinx.android.synthetic.main.print_preview.*
import kotlinx.android.synthetic.main.print_preview.view.*


class MainActivityPrint : Activity() {
    internal lateinit var mScan: Button
    internal lateinit var mPrint: Button
    internal lateinit var mDisc: Button
    internal var mBluetoothAdapter: BluetoothAdapter? = null
    private val applicationUUID = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB")
    private var mBluetoothConnectProgressDialog: ProgressDialog? = null
    private var mBluetoothSocket: BluetoothSocket? = null


    public override fun onCreate(mSavedInstanceState: Bundle?) {
        super.onCreate(mSavedInstanceState)
        setContentView(R.layout.print_preview)
                        var BILL = ""

                        BILL = ("          INOB Softwares           \n"+
                                "          Calicut India            \n"+
                                "          www.ionob.com            \n")
                        BILL = BILL + "-----------------------------------------------\n"


                        BILL = BILL + String.format("%1$-10s %2$10s %3$13s %4$10s", "Item", "Qty", "Rate", "Totel")
                        BILL = BILL + "\n"
                        BILL = BILL + "-----------------------------------------------"
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-001", "5", "10", "50.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-002", "10", "5", "50.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-003", "20", "10", "200.00")
                        BILL = BILL + "\n " + String.format("%1$-10s %2$10s %3$11s %4$10s", "item-004", "50", "10", "500.00")

                        BILL = BILL + "\n-----------------------------------------------"
                        BILL = BILL + "\n\n "

                        BILL = BILL + "                   Total Qty:" + "      " + "85" + "\n"
                        BILL = BILL + "                   Total Value:" + "     " + "700.00" + "\n"

                        BILL = BILL + "-----------------------------------------------\n"
                        BILL = BILL + "\n\n "
//                        os.write(BILL.toByteArray())
//                        //This is printer specific code you can comment ==== > Start
//
//                        // Setting height
//                        val gs = 29
//                        os.write(intToByteArray(gs).toInt())
//                        val h = 104
//                        os.write(intToByteArray(h).toInt())
//                        val n = 162
//                        os.write(intToByteArray(n).toInt())
//
//                        // Setting Width
//                        val gs_width = 29
//                        os.write(intToByteArray(gs_width).toInt())
//                        val w = 119
//                        os.write(intToByteArray(w).toInt())
//                        val n_width = 2
//                        os.write(intToByteArray(n_width).toInt())




        print_preview.text=BILL
    }// onCreate



    override fun onBackPressed() {

        finish()
    }


}
