/* Copyright 2017 Mickael Gaillard <mick.gaillard@gmail.com>
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
package org.ros2.rcljava.demo.timers;

import java.util.concurrent.TimeUnit;

import org.ros2.rcljava.RCLJava;
import org.ros2.rcljava.node.NativeNode;
import org.ros2.rcljava.node.Node;
import org.ros2.rcljava.time.WallTimer;
import org.ros2.rcljava.time.WallTimerCallback;

public class OneOffTimer {

    public static class OneOffTimerNode extends NativeNode {
        private int count = 0;
        private final WallTimer periodicTimer;
        private WallTimer oneOffTimer;

        public OneOffTimerNode() {
            super("one_off_timer");

            this.periodicTimer = this.createWallTimer(2, TimeUnit.SECONDS, new WallTimerCallback() {

                @Override
                public void tick() {
                    System.out.println("in periodic_timer callback");

                    if (OneOffTimerNode.this.count++ % 3 == 0) {
                        System.out.println("  resetting one off timer");
                        OneOffTimerNode.this.oneOffTimer =
                                OneOffTimerNode.this.createWallTimer(1, TimeUnit.SECONDS, new WallTimerCallback() {

                            @Override
                            public void tick() {
                                System.out.println("in one_off_timer callback");
                                  OneOffTimerNode.this.oneOffTimer.cancel();

                            }
                        });
                    } else {
                        System.out.println("  not resetting one off timer");
                    }
                }
            });
        }

        @Override
        public void dispose() {
            this.periodicTimer.dispose();
            this.oneOffTimer.dispose();
            super.dispose();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Initialize RCL
        RCLJava.rclJavaInit(args);
        Node node = new OneOffTimerNode();

        RCLJava.spin(node);

        node.dispose();
        RCLJava.shutdown();
    }
}
