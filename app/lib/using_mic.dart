import 'dart:async';
import 'dart:typed_data';

import 'package:mic_stream/mic_stream.dart';

Stream<Uint8List>? stream;
StreamSubscription<List<int>>? listener;

Future<void> mic_input() async {
  // Init a new Stream
  stream = await MicStream.microphone(sampleRate: 44100);
  // Start listening to the stream
  listener = stream!.listen((samples) => print(samples));
}

void mic_stop(){
  listener!.cancel();
}