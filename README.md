# Random Redstone
A set of blocks and items that provide a variety of functionality related to redstone.

## Information

Currently only implements the Variable Switch, which is a switch with variable output.
## Copyright and license

Copyright 2014 Joshua R. Rodgers

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this work except in compliance with the License.
You may obtain a copy of the License in the LICENSE file, or at:

  [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Current Ideas for the Future
Block Ideas:
* Redstone Lock & Key
* Diode
    - No-delay, directional redstone block
* Signal Inverter
    - O = 15 - I
* Analog Counter
    - Outputs a signal between 0 and 15, adjusted up or down by pulsing the I+ and I- sides
* Capacitor
    - Block the charges to some capacity while being fed a redstone signal and then discharges from 15 to 0 over time
    - The wave form of the discharge is selectable from linear, cubic, etc
* Variable repeater
    - Adjustable output strength with adjustable repeater delay
* Adder
    - O = MIN(I1 + I2, 15)
* Wave generator
    - Generates sine and square waves on an adjustable period and amplitude
* High-pass Filter
    - Filters out incoming signals below a threshold
* Low-pass Filter
    - Filters out incoming signals above a threshold
* Band-pass Filter
    - Filters out signals outside a certain range of strengths
* Edge Detectors
    - Pulse on rising or falling edge.
* Pulse Lengtheners
    - Lengthen the input pulse by a set amount of time
* Signal Strength Randomizer
    - Output a random signal strength when pulsed
