import {Component} from '@angular/core';
import {PollApiService} from "./services/poll-api.service";
import {Poll} from "./model/poll";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  poll?: Poll;

  constructor(private pollApi: PollApiService) {
    this.pollApi.getActivePoll().subscribe(data => {
      this.poll = data;
    });
  }

}
