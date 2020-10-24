/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.bstguesser;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class TreeNode {
    private static final int SIZE = 60;
    private static final int MARGIN = 20;
    private int value, height;
    protected TreeNode left, right;
    private int balanceFactor;
    private boolean showValue, isBalanced;
    private int x, y;
    private int color = Color.rgb(150, 150, 250);

    public TreeNode(int value, int height){
        this.value = value;
        this.height = height;
        showValue = false;
        isBalanced = true;
        balanceFactor = 0;
        left = null;
        right = null;
    }

    public int getHeight(TreeNode node) {
        if(node == null)    return 0;
        return node.height;
    }

    public TreeNode insert(int valueToInsert, TreeNode node, int height) {

        if(valueToInsert<=value){
            if(left == null){
                left = new TreeNode(valueToInsert, height);

            }else{
                left = left.insert(valueToInsert, left, height+1);
            }
        }
        else{
            if(right == null){
                right = new TreeNode(valueToInsert, height);
            }else{
                right = right.insert(valueToInsert, right, height+1);
            }
        }

        node = checkBalance(valueToInsert, node);

        changeHeight(node);

        return node;
    }

    private TreeNode checkBalance(int valueToInsert, TreeNode node) {
        balanceFactor = getBalance(this);
        isBalanced = isBalanced(this);
        if(!isBalanced){
            //check for the case of LR, RL, LL, RR


            this.isBalanced = true;
            switch (checkCase(this, valueToInsert)){
                case "LL": node = LL(this);break;
                case "RR": node = RR(this);break;
                case "LR": node = LR(this);break;
                case "RL": node = RL(this); break;
            }

        }
        return node;
    }

    private TreeNode RL(TreeNode treeNode) {
        Log.i(TAG, "RL:" + value);
        treeNode.right = LL(treeNode.right);
        treeNode = RR(treeNode);
        return treeNode;
    }

    private TreeNode LR(TreeNode treeNode) {
        Log.i(TAG, "LR:" + value);
        treeNode.left = RR(treeNode.left);
        treeNode = LL(treeNode);
        return treeNode;

    }

    private TreeNode RR(TreeNode treeNode) {
        Log.i(TAG, "RR:" + value);
        //changes occur in this and left of this so store this in a and left in b and then ;
        //here change
        /*
        *   z                       y
        *  / \                     / \
        * T   y         =>        z   x
        *    / \                 / \
        *   P   x               T   P
        *
        * */
        TreeNode z = treeNode;
        TreeNode y = treeNode.right;
        z.right = y.left;
        y.left = z;

        //change balance factor and height of every node;

        changeHeight(z);
        changeHeight(y);

        return y;

    }

    private TreeNode LL(TreeNode treeNode) {
        Log.i(TAG, "LL:" + value);
        /*
         * In LL - a             b
         *        / \           / \
         *       b   x   =>    c   a
         *      / \               / \
         *     c   y             y   x
         *
         * this means changes occur in right of b and left of a
         */
        //changes occur in this and left of this so store this in a and left in b and then ;
        TreeNode a = treeNode;
        TreeNode b = treeNode.left;
        a.left = b.right;
        b.right = a;

        //change balance factor and height of every node;

        changeHeight(a);
        changeHeight(b);

        return b;

    }

    public void changeHeight(TreeNode node){
        node.height = 1 + Math.max(getHeight(node.left), getHeight(node.right));
    }

    private String checkCase(TreeNode treeNode, int valueToInsert) {

        if(getBalance(treeNode)<-1 && treeNode.left.value > valueToInsert)
            return "LL";
        if(getBalance(treeNode)<-1 && treeNode.left.value < valueToInsert)
            return "LR";
        if(getBalance(treeNode)>1 && treeNode.right.value > valueToInsert)
            return "RL";
        if(getBalance(treeNode)>1 && treeNode.right.value < valueToInsert)
            return "RR";
        return null;
    }

    public boolean isBalanced(TreeNode node){

        boolean result = true;

        if(Math.abs(balanceFactor)>1){
            result = false;
        }
        return result;

    }

    private int getBalance(TreeNode node) {

        if(node == null)    return 0;
        return getMaxHeight(node.right) - getMaxHeight(node.left);

    }

    public int getMaxHeight(TreeNode node){

        if(node == null)    return 0;

        return Math.max(getMaxHeight(node.left), getMaxHeight(node.right)) + 1;

    }

    public int getValue() {
        return value;
    }

    public void positionSelf(int x0, int x1, int y) {
        this.y = y;
        x = (x0 + x1) / 2;

        if(left != null) {
            left.positionSelf(x0, right == null ? x1 - 2 * MARGIN : x, y + SIZE + MARGIN);
        }
        if (right != null) {
            right.positionSelf(left == null ? x0 + 2 * MARGIN : x, x1, y + SIZE + MARGIN);
        }
    }

    public void draw(Canvas c) {
        Paint linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(3);
        linePaint.setColor(Color.GRAY);
        if (left != null)
            c.drawLine(x, y + SIZE/2, left.x, left.y + SIZE/2, linePaint);
        if (right != null)
            c.drawLine(x, y + SIZE/2, right.x, right.y + SIZE/2, linePaint);

        Paint fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(color);
        c.drawRect(x-SIZE/2, y, x+SIZE/2, y+SIZE, fillPaint);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(SIZE * 2/3);
        paint.setTextAlign(Paint.Align.CENTER);
        c.drawText(showValue ? String.valueOf(value) : "?", x, y + SIZE * 3/4, paint);

        //I removed this height because it was unnecessary

//        if (height > 0) {
//            Paint heightPaint = new Paint();
//            heightPaint.setColor(Color.MAGENTA);
//            heightPaint.setTextSize(SIZE * 2 / 3);
//            heightPaint.setTextAlign(Paint.Align.LEFT);
//            c.drawText(String.valueOf(height), x + SIZE / 2 + 10, y + SIZE * 3 / 4, heightPaint);
//        }

        if (left != null)
            left.draw(c);
        if (right != null)
            right.draw(c);
    }

    public int click(float clickX, float clickY, int target) {
        int hit = -1;
        if (Math.abs(x - clickX) <= (SIZE / 2) && y <= clickY && clickY <= y + SIZE) {
            if (!showValue) {
                if (target != value) {
                    color = Color.RED;
                } else {
                    color = Color.GREEN;
                }
            }
            showValue = true;
            hit = value;
        }
        if (left != null && hit == -1)
            hit = left.click(clickX, clickY, target);
        if (right != null && hit == -1)
            hit = right.click(clickX, clickY, target);
        return hit;
    }

    public void invalidate() {
        color = Color.CYAN;
        showValue = true;
    }
}
