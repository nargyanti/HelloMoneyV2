/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aplas.hellomoneyv2;

import static com.google.common.truth.Truth.assertThat;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.aplas.hellomoneyv2.tflite.Classifier;
import org.aplas.hellomoneyv2.tflite.Classifier.Device;
import org.aplas.hellomoneyv2.tflite.Classifier.Model;
import org.aplas.hellomoneyv2.tflite.Classifier.Recognition;

/** Golden test for Image Classification Reference app. */
@RunWith(AndroidJUnit4.class)
public class ClassifierTest {

  @Rule
  public ActivityTestRule<ClassifierActivity> rule =
      new ActivityTestRule<>(ClassifierActivity.class);

  private static final String[] INPUTS = {"limaribu.jpeg"};
  private static final String[] GOLDEN_OUTPUTS_SUPPORT = {"limaribu.txt"};
  private static final String[] GOLDEN_OUTPUTS_TASK = {"limaribu_task.txt"};

  @Test
  public void classificationResultsShouldNotChange() throws IOException {
    ClassifierActivity activity = rule.getActivity();
    Classifier classifier = Classifier.create(activity, Model.FLOAT_MOBILENET, Device.CPU, 1);
    for (int i = 0; i < INPUTS.length; i++) {
      String imageFileName = INPUTS[i];
      String goldenOutputFileName;

      goldenOutputFileName = GOLDEN_OUTPUTS_SUPPORT[i];

      Bitmap input = loadImage(imageFileName);
      List<Recognition> goldenOutput = loadRecognitions(goldenOutputFileName);

      List<Recognition> result = classifier.recognizeImage(input, 0);
      Iterator<Recognition> goldenOutputIterator = goldenOutput.iterator();

      Assert.assertTrue(goldenOutputIterator.hasNext());
      Recognition expected = goldenOutputIterator.next();
      assertThat(result.get(0).getTitle()).isEqualTo(expected.getTitle());
      assertThat(result.get(0).getConfidence()).isWithin(0.01f).of(expected.getConfidence());
    }
  }

  private static Bitmap loadImage(String fileName) {
    AssetManager assetManager =
        InstrumentationRegistry.getInstrumentation().getContext().getAssets();
    InputStream inputStream = null;
    try {
      inputStream = assetManager.open(fileName);
    } catch (IOException e) {
      Log.e("Test", "Cannot load image from assets");
    }
    return BitmapFactory.decodeStream(inputStream);
  }

  private static List<Recognition> loadRecognitions(String fileName) {
    AssetManager assetManager =
        InstrumentationRegistry.getInstrumentation().getContext().getAssets();
    InputStream inputStream = null;
    try {
      inputStream = assetManager.open(fileName);
    } catch (IOException e) {
      Log.e("Test", "Cannot load probability results from assets");
    }
    Scanner scanner = new Scanner(inputStream);
    List<Recognition> result = new ArrayList<>();
    while (scanner.hasNext()) {
      String category = scanner.next();
      category = category.replace('_', ' ');
      if (!scanner.hasNextFloat()) {
        break;
      }
      float probability = scanner.nextFloat();
      Recognition recognition = new Recognition(null, category, probability, null);
      result.add(recognition);
    }
    return result;
  }
}
