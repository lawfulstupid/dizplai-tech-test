import {Component} from '@angular/core';
import {PollApiService} from "./services/poll-api.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  data: string = '';

  constructor(private pollApi: PollApiService) {
    this.pollApi.getActivePoll().subscribe(data => {
      this.data = data;
    });
  }

}
