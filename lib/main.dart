import 'package:flutter/material.dart';
import 'package:pay_sdk_example_flutter/android_methods.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        title: "Pay example",
        debugShowCheckedModeBanner: false,
        theme: ThemeData(
            primarySwatch: Colors.blue,
            useMaterial3: true,
            cardTheme: const CardTheme(
                clipBehavior: Clip.antiAliasWithSaveLayer,
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.all(Radius.circular(10))))),
        home: const MainPage());
  }
}

class MainPage extends StatefulWidget {
  const MainPage({Key? key}) : super(key: key);

  @override
  State<MainPage> createState() => _MainPageState();
}

class _MainPageState extends State<MainPage> {
  final _textController = TextEditingController();

  Widget _cardPay() {
    return Card(
        child: InkWell(
            onTap: _openPaySdk,
            child: Padding(
                padding: const EdgeInsets.all(15),
                child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: const [
                      Icon(Icons.payment, size: 50),
                      SizedBox(height: 10),
                      Text("Open Pay SDK")
                    ]))));
  }

  Widget _textFieldAmount() {
    return Padding(
        padding: const EdgeInsets.symmetric(horizontal: 15),
        child: TextFormField(
          controller: _textController,
          keyboardType: const TextInputType.numberWithOptions(decimal: true),
          decoration: const InputDecoration(
              border: UnderlineInputBorder(), labelText: 'Enter the amount'),
        ));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: SafeArea(
            child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [Center(child: _cardPay()), _textFieldAmount()])));
  }

  Future<void> _openPaySdk() async {
    try {
      final amount = double.tryParse(_textController.text);
      if (amount == null) throw "Invalid amount";
      final ticket =
          await AndroidMethods.makePayOliPay(mount: amount, reference: "TEST");
      if (mounted) {
        await dialogInformSomething(context,
            title: "Ticket", content: ticket.toString());
      }
    } catch (error) {
      if (mounted) {
        await dialogInformSomething(context,
            title: "Error", content: error.toString());
      }
    }
  }

  static Future<void> dialogInformSomething(final BuildContext context,
      {final String? title, final String? content}) async {
    return await showDialog(
        context: context,
        builder: (context) => AlertDialog(
                title: (title != null) ? Text(title.trim()) : null,
                content: (content != null) ? Text(content.trim()) : null,
                actions: [
                  TextButton(
                      onPressed: () => Navigator.of(context).pop(),
                      child: const Text("OK"))
                ]));
  }
}
