package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

public class Emojifier {

    private String number_of_Faces_logTag = "Number of Faces";
    String Smiling_logTag = "Smiling";
    String LeftEyeOpen_logTag = "Left_Eye_Open";
    String RightEyeOpen_logTag = "Right_Eye_Open";
    String Emoji_Tag = "Emoji";
    private static final float EMOJI_SCALE_FACTOR = .9f;
    Bitmap result_Bitmap;

    public Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap) {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(bitmap).build();
        SparseArray<Face> faces = detector.detect(frame);
        Log.i(number_of_Faces_logTag, faces.size() + "");
        result_Bitmap = bitmap;

        if (faces.size() == 0) {
            Toast.makeText(context, R.string.no_faces, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); ++i) {
                Bitmap emojiBitmap;
                Face face = faces.valueAt(i);
                whichEmoji(face);
                switch (whichEmoji(face)) {
                    case smiling:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
                        break;
                    case frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frown);
                        break;
                    case left_wink:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwink);

                        break;
                    case right_wink:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwink);

                        break;
                    case close_eye_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);

                        break;
                    case closed_eye_smiling:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_smile);

                        break;
                    case left_wink_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwinkfrown);

                        break;
                    case right_wink_frowning:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwinkfrown);

                        break;
                    default:
                        emojiBitmap = null;
                        Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
                }

                // Add the emojiBitmap to the proper position in the original image
                result_Bitmap = addBitmapToFace(result_Bitmap, emojiBitmap, face);
            }
        }

        // Release the detector
        detector.release();
        return result_Bitmap;
    }

    /**
     * Combines the original picture with the emoji bitmaps
     *
     * @param backgroundBitmap The original picture
     * @param emojiBitmap      The chosen emoji
     * @param face             The detected face
     * @return The final bitmap, including the emojis over the faces
     */
    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }


    public Emoji whichEmoji(Face face) {
        float Smiling = face.getIsSmilingProbability();
        float LeftEyeOpen = face.getIsLeftEyeOpenProbability();
        float RightEyeOpen = face.getIsRightEyeOpenProbability();


        boolean is_Smiling = face.getIsSmilingProbability() > .15;

        boolean is_LeftEyeOpen = face.getIsLeftEyeOpenProbability() < .5;
        boolean is_RightEyeOpen = face.getIsRightEyeOpenProbability() < .5;

        Log.d(Emoji_Tag, "is_Smiling" + face.getIsSmilingProbability());
        Log.d(Emoji_Tag, "is_LeftEyeOpen" + face.getIsLeftEyeOpenProbability());
        Log.d(Emoji_Tag, "is_RightEyeOpen" + face.getIsRightEyeOpenProbability());

//        if (is_Smiling) {
//
//
//            if (is_LeftEyeOpen) {
//                if (is_RightEyeOpen) {
//                    Log.d(Emoji_Tag, Emoji.smiling + "");
//                } else {
//                    Log.d(Emoji_Tag, Emoji.right_wink + "");
//                }
//            } else {
//                if (is_RightEyeOpen) {
//                    Log.d(Emoji_Tag, Emoji.left_wink + "");
//                } else {
//                    Log.d(Emoji_Tag, Emoji.closed_eye_smiling + "");
//                }
//            }
//
//
//        } else {
//            if (is_LeftEyeOpen) {
//                if (is_RightEyeOpen) {
//                    Log.d(Emoji_Tag, Emoji.frowning + "");
//                } else {
//                    Log.d(Emoji_Tag, Emoji.right_wink_frowning + "");
//                }
//            } else {
//                if (is_RightEyeOpen) {
//                    Log.d(Emoji_Tag, Emoji.left_wink_frowning + "");
//                } else {
//                    Log.d(Emoji_Tag, Emoji.close_eye_frowning + "");
//                }
//
//
//            }
//
//
//        }
        Emoji emoji;

        if (is_Smiling) {
            if (is_LeftEyeOpen && !is_RightEyeOpen) {
                emoji = Emoji.left_wink;
            } else if (is_RightEyeOpen && !is_LeftEyeOpen) {
                emoji = Emoji.right_wink;
            } else if (is_LeftEyeOpen) {
                emoji = Emoji.closed_eye_smiling;
            } else {
                emoji = Emoji.smiling;
            }
        } else {
            if (is_LeftEyeOpen && !is_RightEyeOpen) {
                emoji = Emoji.left_wink_frowning;
            } else if (is_RightEyeOpen && !is_LeftEyeOpen) {
                emoji = Emoji.right_wink_frowning;
            } else if (is_LeftEyeOpen) {
                emoji = Emoji.close_eye_frowning;
            } else {
                emoji = Emoji.frowning;
            }
        }

        Log.d(Emoji_Tag, "whichEmoji: " + emoji.name());

        Log.d(Smiling_logTag, "smilingProb = " + Smiling);
        Log.d(LeftEyeOpen_logTag, "leftEyeOpenProb = " + LeftEyeOpen);
        Log.d(RightEyeOpen_logTag, "rightEyeOpenProb = " + RightEyeOpen);

        return emoji;

    }


    private enum Emoji {
        smiling,
        frowning,
        left_wink,
        right_wink,
        left_wink_frowning,
        right_wink_frowning,
        closed_eye_smiling,
        close_eye_frowning

    }

}
