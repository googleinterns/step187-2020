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

const FLAG_LOCAL = 1;
/** 
 * When testing locally, need to append host that python service 
 * runs on to requests.
 */
const LOCAL = FLAG_LOCAL === 1 ? 'http://localhost:8889/' : '';

/** Send GET request to trigger cron job to run. */
const runJob = async () => {
  const response = await fetch('/blackswan/test');
  const message = await response.text();
  console.log("job ran: " + message);
}

/** Send GET request to load updated data into Cloud Storage. */
const loadData = async (time) => {
  const response = await fetch(LOCAL + '/python/get-data?time=' + time);
  const message = await response.text();
  console.log("loaded data " + time + ": " + message);
}

/** Load window and add interaction to different components on the page. */
window.onload = () => {
  const run_job_button = document.getElementsByClassName("run-job-button")[0];
  const load_data_1 = document.getElementsByClassName("load-data-1")[0];
  const load_data_2 = document.getElementsByClassName("load-data-2")[0];
  const load_data_3 = document.getElementsByClassName("load-data-3")[0];
  
  run_job_button.onclick = () => runJob();
  load_data_1.onclick = () => loadData(1);
  load_data_2.onclick = () => loadData(2);
  load_data_3.onclick = () => loadData(3);
}
