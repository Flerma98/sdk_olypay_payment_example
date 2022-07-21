package com.flerma.pay_sdk_example_flutter

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant
import mx.qpay.client.QpAmbiente
import mx.qpay.controller.*

abstract class OliPaySdkData {
    companion object {
        const val user = "623"
        const val password = "i5m@3*7R#"
        val ambient = QpAmbiente.TEST
        val locale = QpLocale.SPANISH
    }
}

abstract class ChannelCommands {
    companion object {
        const val appChannel = "flutter_app/kotlin"
        const val makePayOliPaySdk = "make-pay-oli-pay-sdk"
    }
}

class MainActivity : FlutterActivity() {
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MethodChannel(
            flutterEngine!!.dartExecutor.binaryMessenger,
            ChannelCommands.appChannel
        ).setMethodCallHandler { call, result ->
            when (call.method) {
                ChannelCommands.makePayOliPaySdk -> {
                    val mount =
                        call.argument<Double>("mount")
                    val reference =
                        call.argument<String>("reference")
                    if (mount != null && reference != null)
                        makePayOliPaySdk(mount, reference, result, this)
                }
            }
        }
    }


    private fun makePayOliPaySdk(
        mount: Double,
        reference: String,
        result: MethodChannel.Result,
        context: Context
    ) {
        try {
            QpayController.init(
                this,
                OliPaySdkData.user,
                OliPaySdkData.password,
                OliPaySdkData.ambient,
                OliPaySdkData.locale, object : QpayControlEventosImpl() {
                    override fun qpMostrarEstadoTexto(resultado: String, codigo: Int) {
                        // Show progress/hint messages to customer
                    }

                    override fun qpRegresaTransaccion(resultado: HashMap<String, String>) {
                        if (resultado["RemoveCardHint"].equals("true")) {
                            Toast.makeText(
                                context,
                                "Retirar tarjeta",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        if (resultado["CVM"].equals("RECABAR_FIRMA")) {
                            //... make sure signature is captured digitaly and sent with QpayController.getInstance().qpRealizaEnviaVoucher(...) and printed on voucher if voucher is printed
                            Toast.makeText(
                                context,
                                "RECABAR_FIRMA",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Transacción realizada correctamete",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun qpInicializado() {
                        val transactionId =
                            QpayController.getInstance().qpRealizaTransaccion(
                                context,
                                OliPaySdkData.user,
                                OliPaySdkData.password,
                                mount,
                                reference,
                                0,
                                0,
                                0
                            )
                        result.success(transactionId)
                    }

                    override fun qpError(
                        resultado: String,
                        codigo: Int,
                        removeCardHint: Boolean,
                        numeroTransaccion: String
                    ) {
                        Toast.makeText(
                            context,
                            resultado,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                })
        } catch (error: RuntimeException) {
            result.error(error.cause.toString(), error.message, error.message)
        } catch (error: QpayControllerAlreadyInitializedException) {
            result.error(error.cause.toString(), error.message, error.message)
        } catch (error: TransactionOngoingException) {
            result.error(error.cause.toString(), error.message, error.message)
        } catch (error: QpayControllerNotInitializedException) {
            result.error(error.cause.toString(), error.message, error.message)
        }
    }
}
