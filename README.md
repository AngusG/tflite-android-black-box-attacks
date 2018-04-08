
Human-Driven Black-Box Attacks in Android
===================================

A TensorFlow-Lite demo for hand drawing targeted or untargeted black-box attacks for the Madry et al. (2018) models, which have been secured with PGD for perturbations bounded by 0.3 in the L_\infty threat model. We've replaced all variables in their pre-trained checkpoints <https://github.com/MadryLab/mnist_challenge> with constants, frozen the graph, and exported to tflite format.

Our concern is that since this defense is typically referred to as "state-of-the-art", and its limitations are only beginning to be explored, people may deploy these models in real world applications under the impression that they should generalize well in practice. In **Adversarial Training Versus Weight Decay**, we argue that studying L_\infty is important for security, but does not say anything about robustness for other types of perturbations we would like our models to be robust to, such as randomly swapping pixels and other Hamming distance (L0 counting "norm") bounded attacks, changing the background intensity, rotating digits, etc. 

We do not invalidate any of the formal claims made by Madry et al. (2018), but we respectfully disagree with their suggestion of increasing model capacity s.t. we can train against a strong adversary. This becomes an arms race to overfit a specific attack, and generalize poorly. This mobile app shows how a human can fairly easily craft targeted attacks without access to the training data, model architecture, parameters, or having to train a substitute model. For an attacker, this is strictly more difficult than in the white-box setting where we may rely on gradient signal for crafting less perceptible adversarial examples.

Toggle between the **naturally** trained and **public PGD** trained models with a menu in 
upper-right corner, and experiment with the brush stroke thickness and Hamming distance few-pixel
attacks to explore this model's weaknesses. Softmax probabilities refresh on finger lift. 

Pre-requisites
--------------

- Android SDK 27
- Android Build Tools v27.0.2
- Android Support Repository
- TensorFlow 1.6.x

Screenshots and Adversarial Examples
-------------

<img src="screenshots/bbox_5_to_9.png" height="400" alt="5-to-9"/> <img src="screenshots/bbox_6_to_2.png" height="400" alt="6-to-2"/> <img src="screenshots/bbox_6_to_3.png" height="400" alt="6-to-3"/> <img src="screenshots/bbox_7_to_2.png" height="400" alt="7-to-2"/> <img src="screenshots/bbox_7_to_conf_2.png" height="400" alt="7-to-conf-2"/> <img src="screenshots/demo_zero_opt.gif" height="400" alt="demo"/> 

Getting Started
---------------

This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

If you've found an error in this sample, please file an issue.

Patches are encouraged, and may be submitted by forking this project and
submitting a pull request through GitHub.

Citation and Acknowledgements
-------

Thanks to Bradley Kennedy for pair-programming in the car on the way to Vector, and to Jonathan MacPherson for enthusiastically setting up the initial chart functionality.

This project is affiliated with **Adversarial Training Versus Weight Decay**, to appear on arXiv shortly:

Bibtex:  
```
@article{galloway2018adversarial,  
  title={Adversarial Training Versus Weight Decay},  
  author={Galloway, Angus and Tanay, Thomas and Taylor, Graham W},  
  journal={arXiv preprint arXiv:xxxx.XXXXX},  
  year={2018}  
}
```
