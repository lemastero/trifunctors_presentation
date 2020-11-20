@@@ slide { color=#242220 }

@@@@ slide

### Resources

* @link[zio-prelude #279](https://github.com/zio/zio-prelude/pull/279) add Bicovariant and RightCovariant 
* @link[zio-prelude #293](https://github.com/zio/zio-prelude/pull/293) add laws for Divariant using methods returning booleans
* @link[zio-prelude #304](https://github.com/zio/zio-prelude/pull/304) add Zivariant
* @link[zio-prelude #291](https://github.com/zio/zio-prelude/issues/291) issue motivating Zivariant
* @link[zio #4220](https://github.com/zio/zio/pull/4220) add support for Divariant laws - LawfulF and LawsF

@@@@

@@@@ slide

Big thank you for @link[Scalac](https://scalac.io/) for giving me time to do OS in ZIO

![september_zio.png](images/september_zio.png)

@@@@

@@@@ slide

@@@@@ fragments
zio-prelude:  
* Divariant (Profunctor, e.g. A => B)
* Bivariant (Bifunctor, e.g. Either, Tuple2)
* RightCovariant == Divariant ∩ Bivariant
* Zivariant == Divariant + Bivariant
design:
* model input, output, errors
* modularity, small building blocks
try izumi
@@@@@

@@@@

@@@