import 'package:flutter/material.dart';
import 'using_mic.dart';

class HomePageApp extends StatefulWidget {
  @override
  State<HomePageApp> createState() => _HomePageApp();
}

class _HomePageApp extends State<HomePageApp> {
  String displayState = " ";
  bool isWork = false;

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(title: Text("위급 상황 감지 어플")),
          body: Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                Text(displayState),
                Switch(
                  value: isWork,
                  onChanged: (value) {
                    if (value) {
                      setState(
                        () {
                          isWork = value;
                          displayState = '경적 감지 중';
                        },
                      );
                      mic_input();
                    } else {
                      setState(
                            () {
                          isWork = value;
                          displayState = '경적 감지 취소';
                        },
                      );
                      mic_stop();
                    }
                  },
                )
              ],
            ),
          )),
    );
  }
}
