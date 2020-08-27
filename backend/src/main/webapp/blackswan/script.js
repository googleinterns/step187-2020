// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/** Fetch data from '/data' to use as innerHTML. */
const runJob = async () => {
  const response = await fetch('/blackswan/test');
  console.log("job ran");
}

const loadData1 = async () => {
  const response = await fetch('/python/get-data?time=1');
  console.log("load data 1");
}

const loadData2 = async () => {
  const response = await fetch('/python/get-data?time=2');
  console.log("load data 2");
}

const loadData3 = async () => {
  const response = await fetch('/python/get-data?time=3');
  console.log("load data 3");
}

/** Load window and adds interaction to different components on the page. */
window.onload = () => {
  const run_job_button = document.getElementsByClassName("run-job-button")[0];
  const load_data_1 = document.getElementsByClassName("load-data-1")[0];
  const load_data_2 = document.getElementsByClassName("load-data-2")[0];
  const load_data_3 = document.getElementsByClassName("load-data-3")[0];
  
  // Shows a random photo when the random option is clicked.
  run_job_button.onclick = () => runJob();
  load_data_1.onclick = () => loadData1();
  load_data_2.onclick = () => loadData2();
  load_data_3.onclick = () => loadData3();
}
